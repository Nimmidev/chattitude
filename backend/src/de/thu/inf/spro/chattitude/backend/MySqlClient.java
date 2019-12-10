package de.thu.inf.spro.chattitude.backend;

import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.Message;
import de.thu.inf.spro.chattitude.packet.User;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySqlClient {
    private Connection mySqlConnection;

    public MySqlClient() {
        connect();
    }

    private void connect() {
        try {
            MariaDbDataSource dataSource = new MariaDbDataSource();
            dataSource.setUser(System.getenv("MYSQL_USER"));
            dataSource.setPassword(System.getenv("MYSQL_PASSWORD"));
            dataSource.setServerName(System.getenv("MYSQL_HOSTNAME"));
            dataSource.setDatabaseName(System.getenv("MYSQL_DATABASE"));

            mySqlConnection = dataSource.getConnection();
            System.out.println("Successfully connected to mysql server");
            createTables();
            try (Statement stmt = mySqlConnection.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SHOW TABLES;")) {
                    System.out.println("Current tables: ");
                    while (rs.next()) {
                        System.out.println("\t" + rs.getString(1));
                    }
                    System.out.println();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to mysql server", e);
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = mySqlConnection.createStatement()) {
            var metaData = mySqlConnection.getMetaData();
            ResultSet res = metaData.getTables(null, null, "Conversation", new String[] {"TABLE"});
            boolean conversationExists = res.next();

            stmt.execute("CREATE TABLE IF NOT EXISTS User (" +
                    "userId INT UNSIGNED AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(32) NOT NULL," +
                    "password VARCHAR(128) NOT NULL," +
                    "registerDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "UNIQUE INDEX `username_UNIQUE` (`username` ASC)" +
                ");");
            stmt.execute("CREATE TABLE IF NOT EXISTS Conversation (" +
                    "`conversationId` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY," +
                    "`lastActivity` TIMESTAMP NOT NULL" +
                    ",`lastMessageId` INT UNSIGNED" +
                ");");
            stmt.execute("CREATE TABLE IF NOT EXISTS ChatMessage (" +
                    "`messageId` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY," +
                    "`conversationId` INT UNSIGNED NOT NULL," +
                    "`fileId` VARCHAR(36) NOT NULL DEFAULT ''," +
                    "`content` TEXT NOT NULL," +
                    "`sender` INT UNSIGNED NOT NULL, " +
                    "`timestamp` TIMESTAMP NOT NULL, " +
                    "INDEX `conversationId_idx` (`conversationId` ASC)," +
                    "CONSTRAINT `CM_conversationId_constr`" +
                        "FOREIGN KEY (`conversationId`) " +
                        "REFERENCES `chattitude`.`Conversation` (`conversationId`) " +
                        "ON DELETE RESTRICT " +
                        "ON UPDATE RESTRICT, " +
                    "INDEX `sender_idx` (`sender` ASC)," +
                    "CONSTRAINT `CM_sender_constr`" +
                        "FOREIGN KEY (`sender`) " +
                        "REFERENCES `chattitude`.`User` (`userId`) " +
                        "ON DELETE RESTRICT " +
                        "ON UPDATE RESTRICT " +
                ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS ConversationMember (" +
                    "`conversationId` INT UNSIGNED NOT NULL," +
                    "`userId` INT UNSIGNED NOT NULL," +
                    "INDEX `constr_conversationId_idx` (`conversationId` ASC)," +
                    "INDEX `constr_userId_idx` (`userId` ASC)," +
                    "CONSTRAINT `CMember_conversationId_constr` " +
                        "FOREIGN KEY (`conversationId`) " +
                        "REFERENCES `chattitude`.`Conversation` (`conversationId`) " +
                        "ON DELETE RESTRICT " +
                        "ON UPDATE RESTRICT," +
                    "CONSTRAINT `CMember_userId_constr` " +
                        "FOREIGN KEY (`userId`) " +
                        "REFERENCES `chattitude`.`User` (`userId`) " +
                        "ON DELETE RESTRICT " +
                        "ON UPDATE RESTRICT" +
                ");");

            if (!conversationExists) {
                // Add foreign key constraint to Conversation because before the Message table didn't exist
                stmt.execute("ALTER TABLE `Conversation` " +
                        "ADD INDEX `CM_lastMessageId_constr_idx` (`lastMessageId` ASC); "
                );

                stmt.execute("ALTER TABLE `Conversation` " +
                        "ADD CONSTRAINT `CM_lastMessageId_constr` " +
                        "FOREIGN KEY (`lastMessageId`) " +
                        "REFERENCES `chattitude`.`ChatMessage` (`messageId`) " +
                        "ON DELETE RESTRICT " +
                        "ON UPDATE RESTRICT;"
                );

            }

            stmt.execute("CREATE TABLE IF NOT EXISTS `FileUpload` ( " +
                    "`fileId` VARCHAR(36) NOT NULL PRIMARY KEY UNIQUE , " +
                    "`data` LONGBLOB NOT NULL " +
                ");");
        }
    }

    public Connection getMySqlConnection() {
        return mySqlConnection;
    }

    public void close() {
        try {
            mySqlConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getUserId(String username){
        String query = "SELECT userId FROM User WHERE username=?;";
        int userId = -1;

        try (PreparedStatement pstmt = mySqlConnection.prepareStatement(query)){
            pstmt.setString(1, username);
            pstmt.execute();
            if(pstmt.getResultSet().next()){
                userId = pstmt.getResultSet().getInt("userId");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return userId;
    }

    public int createConversation(){
        String insertQuery = "INSERT INTO Conversation (lastActivity) VALUES (now());";
        String selectQuery = "SELECT LAST_INSERT_ID();";
        int conversationId = -1;

        try {
            mySqlConnection.setAutoCommit(false);

            try (PreparedStatement insertPstmt = mySqlConnection.prepareStatement(insertQuery)){
                insertPstmt.execute();

                try (PreparedStatement selectPstmt = mySqlConnection.prepareStatement(selectQuery)){
                    selectPstmt.execute();
                    mySqlConnection.commit();

                    if(selectPstmt.getResultSet() != null && selectPstmt.getResultSet().next()){
                        conversationId = selectPstmt.getResultSet().getInt("LAST_INSERT_ID()");
                    }
                }
            } catch (SQLException e){
                mySqlConnection.rollback();
                e.printStackTrace();
            } finally {
                mySqlConnection.setAutoCommit(true);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }

        return conversationId;
    }

    public void addUserToConversation(int conversationId, int userId){
        String query = "INSERT INTO ConversationMember (conversationId, userId) VALUES (?, ?);";

        try (PreparedStatement pstmt = mySqlConnection.prepareStatement(query)){
            pstmt.setInt(1,  conversationId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void removeUserFromConversation(int userId, int conversationId){
        String query = "DELETE FROM ConversationMember WHERE conversationId=? AND userId=?;";

        try (PreparedStatement pstmt = mySqlConnection.prepareStatement(query)){
            pstmt.setInt(1,  conversationId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public List<Conversation> getUserConversations(int userId){
        String query = "SELECT Conversation.conversationId, Conversation.lastActivity, User.userId, User.username, " +
                "ChatMessage.messageId, ChatMessage.content, ChatMessage.fileId, ChatMessage.timestamp FROM ConversationMember " +
                "INNER JOIN Conversation ON Conversation.conversationId = ConversationMember.conversationId " +
                "INNER JOIN User ON User.userId = ConversationMember.userId " +
                "INNER JOIN ChatMessage ON ChatMessage.messageId = Conversation.lastMessageId " +
                "WHERE ConversationMember.userId = ?;";

        try (PreparedStatement pstmt = mySqlConnection.prepareStatement(query)){
            pstmt.setInt(1,  userId);
            pstmt.execute();
            return getUserConversationsResult(pstmt.getResultSet());
        } catch (SQLException e){
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private List<Conversation> getUserConversationsResult(ResultSet resultSet) throws SQLException {
        List<Conversation> conversations = new ArrayList<>();

        while(resultSet.next()){
            int conversationId = resultSet.getInt("conversationId");
            int userId = resultSet.getInt("userId");
            int messageId = resultSet.getInt("messageId");
            long lastActivity = resultSet.getTimestamp("lastActivity").getTime();
            long timestamp = resultSet.getTimestamp("timestamp").getTime();
            String fileId = new String(resultSet.getBytes("fileId"));
            String content = resultSet.getString("content");
            String username = resultSet.getString("username");
            User user = new User(userId, username);
            Message message = new Message(messageId, conversationId, fileId, content, timestamp, user);
            conversations.add(new Conversation(conversationId, lastActivity, message));
        }

        return conversations;
    }

    public boolean saveMessage(Message message){
        try {
            mySqlConnection.setAutoCommit(false);

            try {
                if(message.getData().length != 0){
                    String fieldId = saveMessageData(message.getData());
                    message.setFieldId(fieldId);
                }

                _saveMessage(message);
                message.setId(getMessageId());
                updateConversationLastMessage(message.getConversationId(), message.getId());

                mySqlConnection.commit();
            } catch (SQLException e){
                mySqlConnection.rollback();
                e.printStackTrace();
                return false;
            } finally {
                mySqlConnection.setAutoCommit(true);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }

        return true;
    }

    private String saveMessageData(byte[] data) throws SQLException {
        String query = "INSERT INTO FileUpload (fileId, data) VALUES (?, ?);";
        UUID fieldId = UUID.randomUUID();

        try (PreparedStatement pstmt = mySqlConnection.prepareStatement(query)){
            pstmt.setString(1, fieldId.toString());
            pstmt.setBytes(2, data);
            pstmt.executeUpdate();
        }

        return fieldId.toString();
    }

    private void _saveMessage(Message message) throws SQLException {
        String query = "INSERT INTO ChatMessage (conversationId, fileId, content, sender, timestamp) VALUES (?, ?, ?, ?, now());";

        try (PreparedStatement pstmt = mySqlConnection.prepareStatement(query)){
            pstmt.setInt(1, message.getConversationId());
            pstmt.setString(2, message.getFileId());
            pstmt.setString(3, message.getContent());
            pstmt.setInt(4, message.getUser().getId());
            pstmt.executeUpdate();
        }
    }

    private int getMessageId() throws SQLException {
        String query = "SELECT LAST_INSERT_ID();";

        try (PreparedStatement pstmt = mySqlConnection.prepareStatement(query)){
            pstmt.execute();

            if(pstmt.getResultSet() != null && pstmt.getResultSet().next()){
                return pstmt.getResultSet().getInt("LAST_INSERT_ID()");
            }
        }

        return -1;
    }

    private void updateConversationLastMessage(int conversationid, int messageId) throws SQLException {
        String query = "UPDATE Conversation SET lastActivity = now(), lastMessageId = ? WHERE conversationId = ?";

        try (PreparedStatement pstmt = mySqlConnection.prepareStatement(query)){
            pstmt.setInt(1, messageId);
            pstmt.setInt(2, conversationid);
            pstmt.executeUpdate();
        }
    }

    public List<User> searchUsers(String searchQuery){
        String query = "SELECT userId, username FROM User WHERE username LIKE ?";

        try (PreparedStatement pstmt = mySqlConnection.prepareStatement(query)){
            pstmt.setString(1, "%" + searchQuery + "%");
            pstmt.execute();

            return getUserSearchResult(pstmt.getResultSet());
        } catch (SQLException e){
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private List<User> getUserSearchResult(ResultSet resultSet) throws SQLException {
        List<User> users = new ArrayList<>();

        while(resultSet.next()){
            int userId = resultSet.getInt("userId");
            String username = resultSet.getString("username");
            users.add(new User(userId, username));
        }

        return users;
    }

    public boolean checkUserInConversation(int userId, int conversationId){
        String query = "SELECT userId FROM ConversationMember WHERE userId = ? AND conversationId = ?";

        try (PreparedStatement pstmt = mySqlConnection.prepareStatement(query)){
            pstmt.setInt(1, userId);
            pstmt.setInt(2, conversationId);
            pstmt.execute();

            if(pstmt.getResultSet() != null){
                return pstmt.getResultSet().next();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    public List<Message> getMessageHistory(int conversationId, int offset, int limit){
        String query = "SELECT User.userId, User.username, ChatMessage.messageId, ChatMessage.content, ChatMessage.conversationId, " +
                "ChatMessage.fileId, ChatMessage.timestamp FROM ChatMessage INNER JOIN User ON User.userId = ChatMessage.sender " +
                "WHERE ChatMessage.conversationId = ? ORDER BY messageId DESC LIMIT ? OFFSET ?;";
        // TODO mit letzter MessageId machen statt mit offset

        try (PreparedStatement pstmt = mySqlConnection.prepareStatement(query)){
            pstmt.setInt(1,  conversationId);
            pstmt.setInt(2,  limit);
            pstmt.setInt(3,  offset);
            pstmt.execute();
            return getMessageHistoryResult(pstmt.getResultSet());
        } catch (SQLException e){
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private List<Message> getMessageHistoryResult(ResultSet resultSet) throws SQLException {
        List<Message> messages = new ArrayList<>();

        while(resultSet.next()){
            int conversationId = resultSet.getInt("conversationId");
            int userId = resultSet.getInt("userId");
            int messageId = resultSet.getInt("messageId");
            long timestamp = resultSet.getTimestamp("timestamp").getTime();
            String fileId = new String(resultSet.getBytes("fileId"));
            String content = resultSet.getString("content");
            String username = resultSet.getString("username");
            User user = new User(userId, username);
            messages.add(new Message(messageId, conversationId, fileId, content, timestamp, user));
        }

        return messages;
    }

    public byte[] getAttachment(String fieldId){
        String query = "SELECT data FROM FileUpload WHERE fileId = ?";

        try (PreparedStatement pstmt = mySqlConnection.prepareStatement(query)){
            pstmt.setString(1, fieldId);
            pstmt.execute();

            if(pstmt.getResultSet() != null && pstmt.getResultSet().next()){
                return pstmt.getResultSet().getBytes("data");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return new byte[]{};
    }

    public void addUser(String username, String password){
        String query = "INSERT INTO User (username, password) VALUES (?,?);";

        try (PreparedStatement pstmt = mySqlConnection.prepareStatement(query)){
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean checkUserExistence(String username){
        String query = "Select username FROM User WHERE username = ?;";
        boolean result = false;

        try (PreparedStatement pstmt = mySqlConnection.prepareStatement(query)){
            pstmt.setString(1, username);
            pstmt.execute();

            if(pstmt.getResultSet() != null){
                result = pstmt.getResultSet().next();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return result;
    }

    public boolean checkUserCredentials(String username, String password){
        String query = "Select username FROM User WHERE username=? AND password=?;";
        boolean result = false;

        try (PreparedStatement pstmt = mySqlConnection.prepareStatement(query)){
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.execute();

            if(pstmt.getResultSet() != null){
                result = pstmt.getResultSet().next();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return result;
    }



}
