package de.thu.inf.spro.chattitude.backend.database;

import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.Message;
import de.thu.inf.spro.chattitude.packet.User;
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
            connection = new ValidConnection(mysqlConnection, this::connect);
            userSQL = new UserSQL(connection);
            conversationSQL = new ConversationSQL(connection);
            conversationMemberSQL = new ConversationMemberSQL(connection);
            fileUploadSQL = new FileUploadSQL(connection);
            messageSQL = new MessageSQL(connection, conversationSQL, fileUploadSQL);
            createTables();
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to mysql server", e);
        }
    }

    private Connection connect() throws SQLException {
        MariaDbDataSource dataSource = new MariaDbDataSource();
        dataSource.setUser(System.getenv("MYSQL_USER"));
        dataSource.setPassword(System.getenv("MYSQL_PASSWORD"));
        dataSource.setServerName(System.getenv("MYSQL_HOSTNAME"));
        dataSource.setDatabaseName(System.getenv("MYSQL_DATABASE"));

        return dataSource.getConnection();
    }

    private void createTables() throws SQLException {
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

    public void close() {
        try {
            connection.get().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- User ---

    public void addUser(String username, String password){
        userSQL.add(username, password);
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

    public boolean checkUserCredentials(String username, String password){
        return userSQL.checkCredentials(username, password);
    }

    // --- Conversation ---

    public int createConversation(String conversationName, int sessionUserId, User[] users){
        int conversationId = conversationSQL.add(conversationName);
        
        if(conversationId != -1){
            addUserToConversation(sessionUserId, conversationId);
            
            for(User user : users) {
                if (user.getId() == sessionUserId) continue;
                addUserToConversation(user.getId(), conversationId);
            }
    
            if(conversationName == null){
                updateConversationAdmin(sessionUserId, conversationId, true);
            }
        }
        
        return conversationId;
    }

    public Conversation getConversation(int conversationId){
        return conversationSQL.get(conversationId);
    }

    public List<User> getConversationUsers(int conversationId){
        return conversationSQL.getUsers(conversationId);
    }

    public List<Conversation> getUserConversations(int userId){
        return conversationSQL.getUserConversations(userId);
    }

    // --- ConversationMember

    public boolean addUserToConversation(int userId, int conversationId){
        return conversationMemberSQL.addToConversation(userId, conversationId);
    }

    public boolean removeUserFromConversation(int userId, int conversationId){
        return conversationMemberSQL.removeFromConversation(userId, conversationId);
    }

    public boolean updateConversationAdmin(int userId, int conversationId, boolean isAdmin){
        return conversationMemberSQL.updateIsAdmin(userId, conversationId, isAdmin);
    }

    public boolean checkUserInConversation(int userId, int conversationId){
        return conversationMemberSQL.checkIfInConversation(userId, conversationId);
    }

    public boolean checkConversationUserIsAdmin(int userId, int conversationId){
        return conversationMemberSQL.checkIsAdmin(userId, conversationId);
    }

    // --- Message ---

    public void saveMessage(Message message) {
        messageSQL.add(message);
    }

    public List<Message> getMessageHistory(int conversationId, int lastMessageId){
        return messageSQL.getHistory(conversationId, lastMessageId);
    }

    // --- FileUpload ---

    public byte[] getAttachment(String fileId){
        return fileUploadSQL.get(fileId);
    }

}
