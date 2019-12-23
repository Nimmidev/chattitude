package de.thu.inf.spro.chattitude.backend.database;

import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.Message;
import de.thu.inf.spro.chattitude.packet.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

final class ConversationSQL extends BaseSQL {

    static final String TABLE_NAME = "Conversation";
    static final String _ID = "conversationId";
    static final String _NAME = "conversationName";
    static final String _LAST_MESSAGE_ID = "lastMessageId";

    private static final String CREATE_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            _ID + " INT UNSIGNED AUTO_INCREMENT PRIMARY KEY," +
            _NAME + " VARCHAR(64)," +
            _LAST_MESSAGE_ID + " INT UNSIGNED" +
            ");";

    private static final String INSERT_CONVERSATION = "" +
        "INSERT INTO " + TABLE_NAME + " (" + _NAME + ") VALUES (?);";

    private static final String ADD_MESSAGE_ID_INDEX = "" +
            "ALTER TABLE " + TABLE_NAME + " ADD INDEX CM_lastMessageId_constr_idx (" + _LAST_MESSAGE_ID + " ASC);";

    private static final String ADD_MESSAGE_ID_CONSTRAINT = "" +
            "ALTER TABLE " + TABLE_NAME + " " +
            "ADD CONSTRAINT CM_lastMessageId_constr " +
            "FOREIGN KEY (" + _LAST_MESSAGE_ID + ") " +
            "REFERENCES " + MessageSQL.TABLE_NAME + " (" + MessageSQL._ID + ") " +
            "ON DELETE RESTRICT " +
            "ON UPDATE RESTRICT;";

    private static final String UPDATE_LAST_MESSAGE = "" + 
            "UPDATE " + TABLE_NAME + " SET " + _LAST_MESSAGE_ID + " = ? WHERE " + _ID + " = ?";

    private static final String GET_CONVERSATION_USERS;
    
    private static final String GET_CONVERSATION__CORE_PART;

    private static final String GET_CONVERSATION__USERS_PART;
    
    private static final String GET_CONVERSATIONS__CORE_PART;
    
    private static final String GET_CONVERSATIONS__USERS_PART;
    
    static {
        String user = UserSQL.TABLE_NAME + ".";
        String msg = MessageSQL.TABLE_NAME + ".";
        String conv = TABLE_NAME + ".";
        String convMemb = ConversationMemberSQL.TABLE_NAME + ".";
        
        String userId = user + UserSQL._ID;
        String username = user + UserSQL._USERNAME;
        String msgUserId = msg + MessageSQL._USER_ID;
        String fileId = msg + MessageSQL._FILE_ID;
        String content = msg + MessageSQL._CONTENT;
        String messageId = msg + MessageSQL._ID;
        String timestamp = msg + MessageSQL._TIMESTAMP;
        String lastMsgId = conv + _LAST_MESSAGE_ID;
        String convId = conv + _ID;
        String convName = conv + _NAME;
        String convMembUId = convMemb + ConversationMemberSQL._USER_ID;
        String convMembConvId = convMemb + ConversationMemberSQL._CONVERSATION_ID;
        
        String corePartKeys = String.join(", ", convId, convName, userId, username, messageId, content, fileId, timestamp);
        
        GET_CONVERSATION_USERS = String.format("SELECT %s, %s FROM %s INNER JOIN %s ON %s = %s WHERE %s = ?", userId, username, 
                ConversationMemberSQL.TABLE_NAME, UserSQL.TABLE_NAME, convMembUId, userId, convMembConvId);
        
        GET_CONVERSATION__CORE_PART =  
                String.format("SELECT %s FROM %s LEFT JOIN %s ON %s = %s LEFT JOIN %s ON %s = %s WHERE %s = ?;", corePartKeys, 
                        ConversationSQL.TABLE_NAME, MessageSQL.TABLE_NAME, messageId, lastMsgId, UserSQL.TABLE_NAME, userId, msgUserId, convId);

        String usersPartKeys = String.join(", ", convMembConvId, userId, username);
        GET_CONVERSATION__USERS_PART = String.format("SELECT %s FROM %s INNER JOIN %s ON %s = %s INNER JOIN %s ON %s = %s WHERE %s = ?;",
                usersPartKeys, ConversationMemberSQL.TABLE_NAME, TABLE_NAME, convId, convMembConvId, UserSQL.TABLE_NAME, 
                userId, convMembUId, convMembConvId);

        GET_CONVERSATIONS__CORE_PART = 
                String.format("SELECT %s FROM %s INNER JOIN %s ON %s = %s LEFT JOIN %s ON %s = %s LEFT JOIN %s ON %s = %s WHERE %s = ?;",
                        corePartKeys, ConversationMemberSQL.TABLE_NAME, TABLE_NAME, convId, convMembConvId, MessageSQL.TABLE_NAME, 
                        messageId, lastMsgId, UserSQL.TABLE_NAME, userId, msgUserId, convMembUId);

        GET_CONVERSATIONS__USERS_PART = String.format("SELECT cm2.%s, %s, %s, cm2.%s FROM %s cm1 INNER JOIN %s ON " + 
                "%s = cm1.%s INNER JOIN %s cm2 ON cm2.%s = %s INNER JOIN %s ON cm2.%s = %s WHERE cm1.%s = ?;", 
                _ID, userId, username,  ConversationMemberSQL._IS_ADMIN,  ConversationMemberSQL.TABLE_NAME, TABLE_NAME, 
                convId, _ID, ConversationMemberSQL.TABLE_NAME, _ID, convId, UserSQL.TABLE_NAME, UserSQL._ID, userId, UserSQL._ID);
    }

    ConversationSQL(ValidConnection connection){
        super(connection);
    }

    void createTable(){
        super.createTable(CREATE_TABLE);
    }

    void addIndexAndConstraints(){
        try(Statement statement = connection.get().createStatement()){
            statement.execute(ADD_MESSAGE_ID_INDEX);
            statement.execute(ADD_MESSAGE_ID_CONSTRAINT);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    int add(String conversationName){
        int conversationId = -1;

        try {
            connection.get().setAutoCommit(false);

            try (PreparedStatement insertPstmt = connection.get().prepareStatement(INSERT_CONVERSATION)){
                insertPstmt.setString(1, conversationName);
                insertPstmt.execute();

                conversationId = getLastInsertId();
            } catch (SQLException e){
                connection.get().rollback();
                e.printStackTrace();
            } finally {
                connection.get().setAutoCommit(true);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }

        return conversationId;
    }

    Conversation get(int conversationId){
        Conversation conversation = null;

        try {
            connection.get().setAutoCommit(false);

            try {
                PreparedStatement pstmtConversation = connection.get().prepareStatement(GET_CONVERSATION__CORE_PART);
                pstmtConversation.setInt(1,  conversationId);
                pstmtConversation.execute();

                PreparedStatement pstmtUsers = connection.get().prepareStatement(GET_CONVERSATION__USERS_PART);
                pstmtUsers.setInt(1,  conversationId);
                pstmtUsers.execute();

                ResultSet conversationResults = pstmtConversation.getResultSet();
                ResultSet usersResults = pstmtUsers.getResultSet();

                if(conversationResults.next()) conversation = getResult(conversationResults);
                if(conversation != null) conversation.setUsers(getUsersResult(usersResults));
                else throw new SQLException("Conversation can't be null");

                connection.get().commit();
            } catch (SQLException e){
                connection.get().rollback();
                e.printStackTrace();
            } finally {
                connection.get().setAutoCommit(true);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }

        return conversation;
    }

    private Conversation getResult(ResultSet resultSet) throws SQLException {
        Message message = null;
        Conversation conversation;

        int conversationId = resultSet.getInt(MessageSQL._CONVERSATION_ID);
        int userId = resultSet.getInt(UserSQL._ID);
        String conversationName = resultSet.getString(ConversationSQL._NAME);
        int messageId = resultSet.getInt(MessageSQL._ID);

        if(!resultSet.wasNull()){
            long timestamp = resultSet.getTimestamp(MessageSQL._TIMESTAMP).getTime();

            byte[] bytes = resultSet.getBytes(FileUploadSQL._ID);
            String fileId = bytes != null ? new String(bytes) : null;
            String content = resultSet.getString(MessageSQL._CONTENT);
            String username = resultSet.getString(UserSQL._USERNAME);
            User user = new User(userId, username);
            message = new Message(messageId, conversationId, fileId, content, timestamp, user);
        }

        conversation = new Conversation(conversationId, conversationName, message);

        return conversation;
    }

    private List<User> getUsersResult(ResultSet resultSet) throws SQLException {
        List<User> users = new ArrayList<>();

        while(resultSet.next()){
            int userId = resultSet.getInt("userId");
            String username = resultSet.getString("username");
            users.add(new User(userId, username));
        }

        return users;
    }

    List<User> getUsers(int conversationId){
        List<User> users = new ArrayList<>();

        try (PreparedStatement pstmt = connection.get().prepareStatement(GET_CONVERSATION_USERS)){
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

    List<Conversation> getUserConversations(int userId){
        try {
            connection.get().setAutoCommit(false);

            try {
                Map<Integer, Conversation> conversations = getUserConversationsWithoutUsers(userId);
                addUsersResultToConversations(userId, conversations);
                connection.get().commit();

                return new ArrayList<>(conversations.values());
            } catch (SQLException e){
                connection.get().rollback();
                e.printStackTrace();
            } finally {
                connection.get().setAutoCommit(true);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private Map<Integer, Conversation> getUserConversationsWithoutUsers(int userId) throws SQLException {
        Map<Integer, Conversation> conversations = new HashMap<>();

        try (PreparedStatement pstmt = connection.get().prepareStatement(GET_CONVERSATIONS__CORE_PART)){
            pstmt.setInt(1,  userId);
            pstmt.execute();

            ResultSet resultSet = pstmt.getResultSet();
            while(resultSet.next()){
                Conversation conversation = getResult(resultSet);
                conversations.put(conversation.getId(), conversation);
            }
        }

        return conversations;
    }

    private void addUsersResultToConversations(int userId, Map<Integer, Conversation> conversations) throws SQLException {
        try (PreparedStatement pstmt = connection.get().prepareStatement(GET_CONVERSATIONS__USERS_PART)){
            pstmt.setInt(1,  userId);
            pstmt.execute();
            addUsersToConversationsResult(userId, pstmt.getResultSet(), conversations);
        }
    }

    private void addUsersToConversationsResult(int ogUserId, ResultSet resultSet, Map<Integer, Conversation> conversations) throws SQLException {
        Map<Integer, List<User>> userMap = new HashMap<>();

        while(resultSet.next()){
            int conversationId = resultSet.getInt("conversationId");
            int userId = resultSet.getInt("userId");
            String username = resultSet.getString("username");
            User user = new User(userId, username);
            List<User> currentUserList = userMap.computeIfAbsent(conversationId, k -> new ArrayList<>());

            currentUserList.add(user);
        }

        Set<Integer> keys = userMap.keySet();
        for(Integer conversationId : keys){
            Conversation conversation = conversations.get(conversationId);
            if(conversation != null){
                List<User> userList = userMap.get(conversationId);
                conversation.setUsers(userList);
                
                if(conversation.getName() == null){
                    if(userList.size() == 2){
                        User other = userList.get(0).getId() == ogUserId ? userList.get(1) : userList.get(0);
                        conversation.setName(other.getName());
                    } else {
                        conversation.setName("Invalid State Chat");
                    }
                }
            }
        }
    }
    
    boolean updateLastMessageId(int conversationId, int messageId){
        try (PreparedStatement pstmt = connection.get().prepareStatement(UPDATE_LAST_MESSAGE)){
            pstmt.setInt(1, messageId);
            pstmt.setInt(2, conversationId);
            pstmt.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        
        return true;
    }

}
