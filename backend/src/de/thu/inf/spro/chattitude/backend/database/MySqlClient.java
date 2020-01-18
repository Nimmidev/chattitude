package de.thu.inf.spro.chattitude.backend.database;

import de.thu.inf.spro.chattitude.backend.Server;
import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.Message;
import de.thu.inf.spro.chattitude.packet.User;
import de.thu.inf.spro.chattitude.packet.packets.ModifyConversationUserPacket;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.*;
import java.util.*;

public class MySqlClient {

    private ValidConnection connection;

    private UserSQL userSQL;
    private ConversationSQL conversationSQL;
    private ConversationMemberSQL conversationMemberSQL;
    private MessageSQL messageSQL;
    private FileUploadSQL fileUploadSQL;

    public MySqlClient() {
        try {
            Connection mysqlConnection = connect();
            connection = new ValidConnection(mysqlConnection, MySqlClient::connect);
            
            userSQL = new UserSQL(connection);
            conversationSQL = new ConversationSQL(connection);
            conversationMemberSQL = new ConversationMemberSQL(connection);
            fileUploadSQL = new FileUploadSQL(connection);
            messageSQL = new MessageSQL(connection, conversationSQL, fileUploadSQL);
            
            createTables(connection, userSQL, conversationSQL, conversationMemberSQL, fileUploadSQL, messageSQL);
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to mysql server", e);
        }
    }

    static void createTables(ValidConnection connection, UserSQL userSQL, ConversationSQL conversationSQL,
                             ConversationMemberSQL conversationMemberSQL, FileUploadSQL fileUploadSQL,
                             MessageSQL messageSQL) throws SQLException {
        
        var metaData = connection.get().getMetaData();
        ResultSet res = metaData.getTables(null, null, "Conversation", new String[] {"TABLE"});
        boolean conversationExists = res.next();

        if(!conversationExists){
            userSQL.createTable();
            conversationSQL.createTable();
            conversationMemberSQL.createTable();
            fileUploadSQL.createTable();
            messageSQL.createTable();

            conversationSQL.addIndexAndConstraints();
        }
    }

    static Connection connect() throws SQLException {
        MariaDbDataSource dataSource = new MariaDbDataSource();
        dataSource.setUser(System.getenv("MYSQL_USER"));
        dataSource.setPassword(System.getenv("MYSQL_PASSWORD"));
        dataSource.setServerName(System.getenv("MYSQL_HOSTNAME"));
        dataSource.setDatabaseName(System.getenv("MYSQL_DATABASE"));

        return dataSource.getConnection();
    }

    static void dropTables(ValidConnection connection){
        String tableStrings = String.join(", ", UserSQL.TABLE_NAME, ConversationSQL.TABLE_NAME, ConversationMemberSQL.TABLE_NAME,
                FileUploadSQL.TABLE_NAME, MessageSQL.TABLE_NAME);

        try(Statement statement = connection.get().createStatement()){
            statement.addBatch("SET FOREIGN_KEY_CHECKS = 0;");
            statement.addBatch(String.format("DROP TABLE IF EXISTS %s;", tableStrings));
            statement.addBatch("SET FOREIGN_KEY_CHECKS = 1;");
            statement.executeBatch();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    
    public void close() {
        try {
            connection.get().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- User ---

    public int addUser(String username, String password){
        return userSQL.add(username, password);
    }

    public int getUserId(String username){
        return userSQL.getId(username);
    }

    public List<User> searchUsers(String searchQuery){
        return userSQL.search(searchQuery);
    }

    public boolean checkUserExistence(String username){
        return userSQL.checkExistence(username);
    }
    
    public boolean checkUserExistence(int userId){
        return userSQL.checkExistence(userId);
    }

    public boolean checkUserCredentials(String username, String password){
        return userSQL.checkCredentials(username, password);
    }
    
    private boolean checkUserExistences(User[] users){
        for(User user : users){
            if(!userSQL.checkExistence(user.getId())) return false;
        }
        return true;
    }

    // --- Conversation ---

    public int createConversation(String conversationName, int sessionUserId, User[] users){
        if(conversationName == null && users.length != 1){
            throw new IllegalStateException("A Single Chat can only be created with exactly two users.");
        } else if(!checkUserExistences(users)){
            throw new IllegalStateException("Can't create chat with invalid user ids.");
        }
        
        int conversationId = conversationSQL.add(conversationName);
        
        if(conversationId != -1){
            conversationMemberSQL.addToConversation(sessionUserId, conversationId);
            
            for(User user : users) {
                if (user.getId() == sessionUserId) continue;
                conversationMemberSQL.addToConversation(user.getId(), conversationId);
            }
    
            if(conversationName != null){
                conversationMemberSQL.updateIsAdmin(sessionUserId, conversationId, true);
            }
        }
        
        return conversationId;
    }

    public Conversation getConversation(int conversationId, int ogUserId){
        return conversationSQL.get(conversationId, ogUserId);
    }

    public List<User> getConversationUsers(int conversationId){
        return conversationSQL.getUsers(conversationId);
    }

    public List<Conversation> getUserConversations(int userId){
        return conversationSQL.getUserConversations(userId);
    }

    // --- ConversationMember

    public boolean modifyConversationUser(ModifyConversationUserPacket.Action action, int sessionUserId, int userId, int conversationId){
        boolean isAdmin = conversationMemberSQL.checkIsAdmin(sessionUserId, conversationId);
        boolean success = false;

        if(isAdmin){
            if(action == ModifyConversationUserPacket.Action.REMOVE){
                success = conversationMemberSQL.removeFromConversation(userId, conversationId);
            } else if(action == ModifyConversationUserPacket.Action.ADD){
                success = conversationMemberSQL.addToConversation(userId, conversationId);
            } else if(action == ModifyConversationUserPacket.Action.PROMOTE_ADMIN){
                success = conversationMemberSQL.updateIsAdmin(userId, conversationId, true);
            } else if(action == ModifyConversationUserPacket.Action.DEMOTE_ADMIN){
                success = conversationMemberSQL.updateIsAdmin(userId, conversationId, false);
            }
        }
        
        return success;
    }

    public boolean checkUserInConversation(int userId, int conversationId){
        return conversationMemberSQL.checkIfInConversation(userId, conversationId);
    }

    // --- Message ---

    public int saveMessage(int sessionUserId, Message message) {
        if(checkUserInConversation(sessionUserId, message.getConversationId())){
            if(message.getData().length < Server.MAX_FILE_UPLOAD_SIZE){
                return messageSQL.add(message);
            }
        }

        return -1;
    }

    public List<Message> getMessageHistory(int sessionUserId, int conversationId, int lastMessageId){
        if(checkUserInConversation(sessionUserId, conversationId)){
            return messageSQL.getHistory(conversationId, lastMessageId);
        }

        return null;
    }

    // --- FileUpload ---

    public byte[] getAttachment(String fileId){
        return fileUploadSQL.get(fileId);
    }

}
