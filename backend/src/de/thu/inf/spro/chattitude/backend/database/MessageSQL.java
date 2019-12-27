package de.thu.inf.spro.chattitude.backend.database;

import de.thu.inf.spro.chattitude.backend.Server;
import de.thu.inf.spro.chattitude.packet.Message;
import de.thu.inf.spro.chattitude.packet.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

final class MessageSQL extends BaseSQL {

    static final String TABLE_NAME = "ChatMessage";
    static final String _ID = "messageId";
    static final String _CONVERSATION_ID = ConversationSQL._ID;
    static final String _FILE_ID = "fileId";
    static final String _CONTENT = "content";
    static final String _USER_ID = UserSQL._ID;
    static final String _TIMESTAMP = "timestamp";

    private static final String CREATE_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            _ID + " INT UNSIGNED AUTO_INCREMENT PRIMARY KEY," +
            _CONVERSATION_ID + " INT UNSIGNED NOT NULL," +
            _FILE_ID + " VARCHAR(36)," +
            _CONTENT + " TEXT NOT NULL," +
            _USER_ID + " INT UNSIGNED NOT NULL, " +
            _TIMESTAMP + " TIMESTAMP NOT NULL, " +
            "INDEX conversationId_idx (" + _CONVERSATION_ID + " ASC)," +
            "CONSTRAINT CM_conversationId_constr " +
            "FOREIGN KEY (" + _CONVERSATION_ID + ") " +
            "REFERENCES " + ConversationSQL.TABLE_NAME + " (" + ConversationSQL._ID + ") " +
            "ON DELETE RESTRICT " +
            "ON UPDATE RESTRICT, " +
            "INDEX user_id_idx (" + _USER_ID + " ASC)," +
            "CONSTRAINT CM_user_id_constr " +
            "FOREIGN KEY (" + _USER_ID + ") " +
            "REFERENCES " + UserSQL.TABLE_NAME + " (" + UserSQL._ID + ") " +
            "ON DELETE RESTRICT " +
            "ON UPDATE RESTRICT, " +
            "CONSTRAINT CM_file_id_constr " +
            "FOREIGN KEY (" + _FILE_ID + ") " +
            "REFERENCES " + FileUploadSQL.TABLE_NAME + " (" + FileUploadSQL._ID + ") " +
            "ON DELETE RESTRICT " +
            "ON UPDATE RESTRICT" +
            ");";

    private static final String INSERT_MESSAGE = "" + 
            "INSERT INTO " + TABLE_NAME + " (" + _CONVERSATION_ID + ", " + _FILE_ID + ", " + _CONTENT + ", " + _USER_ID + 
            ", " + _TIMESTAMP + ") VALUES (?, ?, ?, ?, now());";
    
    private static final String GET_HISTORY;

    static {
        String user = UserSQL.TABLE_NAME;
        String userId = user + ". " + UserSQL._ID;
        String username = user + ". " + UserSQL._USERNAME;
        String messageId = TABLE_NAME + "." + _ID;
        String content = TABLE_NAME + "." + _CONTENT;
        String conversationId = TABLE_NAME + "." + _CONVERSATION_ID;
        String fileId = TABLE_NAME + "." + _FILE_ID;
        String timestamp = TABLE_NAME + "." + _TIMESTAMP;
        String chatUserId = TABLE_NAME + "." + _USER_ID;
        
        String tmp = String.join(", ", userId, username, messageId, content, conversationId, fileId, timestamp);
        
        GET_HISTORY = "SELECT " + tmp + " FROM " + TABLE_NAME + " INNER JOIN " + UserSQL.TABLE_NAME + " ON " + userId + 
                " = " + chatUserId + " WHERE " + conversationId + " = ? AND " + messageId + " < ? ORDER BY " + _ID + 
                " DESC LIMIT ?;";
    }
    
    private ConversationSQL conversationSQL;
    private FileUploadSQL fileUploadSQL;
    
    MessageSQL(ValidConnection connection, ConversationSQL conversationSQL, FileUploadSQL fileUploadSQL){
        super(connection);
        this.conversationSQL = conversationSQL;
        this.fileUploadSQL = fileUploadSQL;
    }

    void createTable(){
        super.createTable(CREATE_TABLE);
    }

    int add(Message message){
        try {
            connection.get().setAutoCommit(false);

            try {
                if(message.getData().length != 0){
                    String fileId = fileUploadSQL.add(message.getData());
                    message.setFileId(fileId);
                }

                insertMessage(message);
                int messageId = getLastInsertId();
                message.setId(messageId);
                conversationSQL.updateLastMessageId(message.getConversationId(), message.getId());

                connection.get().commit();
                return messageId;
            } catch (SQLException e){
                connection.get().rollback();
                e.printStackTrace();
            } finally {
                connection.get().setAutoCommit(true);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }

        return -1;
    }

    private void insertMessage(Message message) throws SQLException {
        try (PreparedStatement pstmt = connection.get().prepareStatement(INSERT_MESSAGE)){
            pstmt.setInt(1, message.getConversationId());
            pstmt.setString(2, message.getFileId());
            pstmt.setString(3, message.getContent());
            pstmt.setInt(4, message.getUser().getId());
            pstmt.executeUpdate();
        }
    }

    List<Message> getHistory(int conversationId, int lastMessageId){
        try (PreparedStatement pstmt = connection.get().prepareStatement(GET_HISTORY)){
            pstmt.setInt(1,  conversationId);
            pstmt.setInt(2,  lastMessageId);
            pstmt.setInt(3,  Server.MESSAGE_HISTORY_FETCH_LIMIT);
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
            byte[] bytes = resultSet.getBytes("fileId");
            String fileId = bytes != null ? new String(bytes) : null;
            String content = resultSet.getString("content");
            String username = resultSet.getString("username");
            User user = new User(userId, username);
            messages.add(new Message(messageId, conversationId, fileId, content, timestamp, user));
        }

        return messages;
    }

}
