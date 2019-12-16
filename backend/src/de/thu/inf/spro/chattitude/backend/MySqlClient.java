package de.thu.inf.spro.chattitude.backend;

import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.Message;
import de.thu.inf.spro.chattitude.packet.User;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.*;
import java.util.*;

public class MySqlClient {
    private Connection mySqlConnection;

    public MySqlClient() {
        try {
            connect();
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to mysql server", e);
        }
    }

    private void connect() throws SQLException {
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
    }

    private Connection getConnection() throws SQLException {
        if (!mySqlConnection.isValid(5))
            connect();
        return mySqlConnection;
    }

    private void createTables() throws SQLException {
        try (Statement stmt = getConnection().createStatement()) {
            var metaData = getConnection().getMetaData();
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
                    "`name` VARCHAR(64) NOT NULL DEFAULT ''" +
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
                    "`isAdmin` BOOLEAN NOT NULL DEFAULT false," +
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

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)){
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

    public int createConversation(String name){
        String insertQuery = "INSERT INTO Conversation (name) VALUES (?);";
        String selectQuery = "SELECT LAST_INSERT_ID();";
        int conversationId = -1;

        try {
            getConnection().setAutoCommit(false);

            try (PreparedStatement insertPstmt = getConnection().prepareStatement(insertQuery)){
                insertPstmt.setString(1, name);
                insertPstmt.execute();

                try (PreparedStatement selectPstmt = getConnection().prepareStatement(selectQuery)){
                    selectPstmt.execute();
                    getConnection().commit();

                    if(selectPstmt.getResultSet() != null && selectPstmt.getResultSet().next()){
                        conversationId = selectPstmt.getResultSet().getInt("LAST_INSERT_ID()");
                    }
                }
            } catch (SQLException e){
                getConnection().rollback();
                e.printStackTrace();
            } finally {
                getConnection().setAutoCommit(true);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }

        return conversationId;
    }

    public void addUserToConversation(int conversationId, int userId){
        String query = "INSERT INTO ConversationMember (conversationId, userId) VALUES (?, ?);";

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)){
            pstmt.setInt(1,  conversationId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void removeUserFromConversation(int userId, int conversationId){
        String query = "DELETE FROM ConversationMember WHERE conversationId=? AND userId=?;";

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)){
            pstmt.setInt(1,  conversationId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updateConversationAdmin(int conversationId, int userId, boolean admin){
        String query = "UPDATE ConversationMember SET isAdmin = ? WHERE userId = ? AND conversationId = ?;";

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)){
            pstmt.setBoolean(1,  admin);
            pstmt.setInt(2, userId);
            pstmt.setInt(3,  conversationId);
            pstmt.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public List<User> getConversationUsers(int conversationId){
        List<User> users = new ArrayList<>();
        String query = "SELECT User.userId, User.username FROM ConversationMember " +
                "INNER JOIN User ON ConversationMember.userId = User.userId " +
                "WHERE ConversationMember.conversationId = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)){
            pstmt.setInt(1,  conversationId);
            pstmt.execute();
            ResultSet resultSet = pstmt.getResultSet();

            while(resultSet.next()){
                int userId = resultSet.getInt("userId");
                String username = resultSet.getString("username");
                users.add(new User(userId, username));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return users;
    }

    public List<Conversation> getUserConversations(int userId){
        try {
            getConnection().setAutoCommit(false);

            try {
                Map<Integer, Conversation> conversations = _getUserConversations(userId);
                _addUsersToConversations(userId, conversations);
                getConnection().commit();

                return new ArrayList<>(conversations.values());
            } catch (SQLException e){
                getConnection().rollback();
                e.printStackTrace();
            } finally {
                getConnection().setAutoCommit(true);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private Map<Integer, Conversation> _getUserConversations(int userId) throws SQLException {
        String query = "SELECT Conversation.conversationId, Conversation.name, User.userId, " +
                "User.username, ChatMessage.messageId, ChatMessage.content, ChatMessage.fileId, ChatMessage.timestamp FROM ConversationMember " +
                "INNER JOIN Conversation ON Conversation.conversationId = ConversationMember.conversationId " +
                "INNER JOIN ChatMessage ON ChatMessage.messageId = Conversation.lastMessageId " +
                "INNER JOIN User ON User.userId = ChatMessage.sender " +
                "WHERE ConversationMember.userId = ?;";

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)){
            pstmt.setInt(1,  userId);
            pstmt.execute();
            return _getUserConversationsResult(pstmt.getResultSet());
        }
    }

    private Map<Integer, Conversation> _getUserConversationsResult(ResultSet resultSet) throws SQLException {
        Map<Integer, Conversation> conversations = new HashMap<>();

        while(resultSet.next()){
            int conversationId = resultSet.getInt("conversationId");
            int userId = resultSet.getInt("userId");
            int messageId = resultSet.getInt("messageId");
            long timestamp = resultSet.getTimestamp("timestamp").getTime();

            String conversationName = resultSet.getString("name");
            String fileId = new String(resultSet.getBytes("fileId"));
            String content = resultSet.getString("content");
            String username = resultSet.getString("username");
            User user = new User(userId, username);
            Message message = new Message(messageId, conversationId, fileId, content, timestamp, user);

            conversations.put(conversationId, new Conversation(conversationId, conversationName, message));
        }

        return conversations;
    }

    private void _addUsersToConversations(int userId, Map<Integer, Conversation> conversations) throws SQLException {
        String query = "SELECT cm2.conversationId, User.userId, User.username FROM `ConversationMember` cm1 " +
                "INNER JOIN Conversation ON Conversation.conversationId = cm1.conversationId " +
                "INNER JOIN ConversationMember cm2 ON cm2.conversationId = Conversation.conversationId " +
                "INNER JOIN User ON cm2.userId = User.userId " +
                "WHERE cm1.userId = ?;";

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)){
            pstmt.setInt(1,  userId);
            pstmt.execute();
            _addUsersToConversationsResult(userId, pstmt.getResultSet(), conversations);
        }
    }

    private void _addUsersToConversationsResult(int ogUserId, ResultSet resultSet, Map<Integer, Conversation> conversations) throws SQLException {
        Map<Integer, List<User>> userMap = new HashMap<>();

        while(resultSet.next()){
            int conversationId = resultSet.getInt("conversationId");
            int userId = resultSet.getInt("userId");
            String username = resultSet.getString("username");
            User user = new User(userId, username);
            List currentUserList = userMap.get(conversationId);

            if(currentUserList == null){
                currentUserList = new ArrayList();
                userMap.put(conversationId, currentUserList);
            }
            currentUserList.add(user);
        }

        Set<Integer> keys = userMap.keySet();
        for(Integer conversationId : keys){
            Conversation conversation = conversations.get(conversationId);
            if(conversation != null){
                List<User> userList = userMap.get(conversationId);
                conversation.setUsers(userList);
                if(userList.size() == 2){
                    User other = userList.get(0).getId() == ogUserId ? userList.get(1) : userList.get(0);
                    conversation.setName(other.getName());
                }
            }
        }
    }

    public boolean saveMessage(Message message){
        try {
            getConnection().setAutoCommit(false);

            try {
                if(message.getData().length != 0){
                    String fieldId = saveMessageData(message.getData());
                    message.setFieldId(fieldId);
                }

                _saveMessage(message);
                message.setId(getMessageId());
                updateConversationLastMessage(message.getConversationId(), message.getId());

                getConnection().commit();
            } catch (SQLException e){
                getConnection().rollback();
                e.printStackTrace();
                return false;
            } finally {
                getConnection().setAutoCommit(true);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }

        return true;
    }

    private String saveMessageData(byte[] data) throws SQLException {
        String query = "INSERT INTO FileUpload (fileId, data) VALUES (?, ?);";
        UUID fieldId = UUID.randomUUID();

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)){
            pstmt.setString(1, fieldId.toString());
            pstmt.setBytes(2, data);
            pstmt.executeUpdate();
        }

        return fieldId.toString();
    }

    private void _saveMessage(Message message) throws SQLException {
        String query = "INSERT INTO ChatMessage (conversationId, fileId, content, sender, timestamp) VALUES (?, ?, ?, ?, now());";

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)){
            pstmt.setInt(1, message.getConversationId());
            pstmt.setString(2, message.getFileId());
            pstmt.setString(3, message.getContent());
            pstmt.setInt(4, message.getUser().getId());
            pstmt.executeUpdate();
        }
    }

    private int getMessageId() throws SQLException {
        String query = "SELECT LAST_INSERT_ID();";

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)){
            pstmt.execute();

            if(pstmt.getResultSet() != null && pstmt.getResultSet().next()){
                return pstmt.getResultSet().getInt("LAST_INSERT_ID()");
            }
        }

        return -1;
    }

    private void updateConversationLastMessage(int conversationid, int messageId) throws SQLException {
        String query = "UPDATE Conversation SET lastMessageId = ? WHERE conversationId = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)){
            pstmt.setInt(1, messageId);
            pstmt.setInt(2, conversationid);
            pstmt.executeUpdate();
        }
    }

    public List<User> searchUsers(String searchQuery){
        String query = "SELECT userId, username FROM User WHERE username LIKE ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)){
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

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)){
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

    public boolean checkUserIsAdmin(int userId, int conversationId){
        String query = "SELECT userId FROM ConversationMember WHERE userId = ? AND conversationId = ? AND isAdmin = true";

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)){
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

    public List<Message> getMessageHistory(int conversationId, int lastMessageId, int limit){
        String query = "SELECT User.userId, User.username, ChatMessage.messageId, ChatMessage.content, ChatMessage.conversationId, " +
                "ChatMessage.fileId, ChatMessage.timestamp FROM ChatMessage INNER JOIN User ON User.userId = ChatMessage.sender " +
                "WHERE ChatMessage.conversationId = ? AND ChatMessage.messageId < ? ORDER BY messageId DESC LIMIT ?;";

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)){
            pstmt.setInt(1,  conversationId);
            pstmt.setInt(2,  lastMessageId);
            pstmt.setInt(3,  limit);
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

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)){
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

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)){
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

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)){
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

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)){
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
