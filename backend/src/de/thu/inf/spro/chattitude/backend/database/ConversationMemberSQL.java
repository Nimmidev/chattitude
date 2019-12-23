package de.thu.inf.spro.chattitude.backend.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

final class ConversationMemberSQL extends BaseSQL {

    static final String TABLE_NAME = "ConversationMember";
    static final String _CONVERSATION_ID = ConversationSQL._ID;
    static final String _USER_ID = UserSQL._ID;
    static final String _IS_ADMIN = "isAdmin";

    private static final String CREATE_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            _CONVERSATION_ID + " INT UNSIGNED NOT NULL," +
            _USER_ID + " INT UNSIGNED NOT NULL," +
            _IS_ADMIN + " BOOLEAN NOT NULL DEFAULT false," +
            "PRIMARY KEY (" + _CONVERSATION_ID + ", " + _USER_ID + "), " +
            "INDEX `constr_conversationId_idx` (" + _CONVERSATION_ID + " ASC)," +
            "INDEX `constr_userId_idx` (" + _USER_ID + " ASC)," +
            "CONSTRAINT `CMember_conversationId_constr` " +
            "FOREIGN KEY (" + _CONVERSATION_ID + ") " +
            "REFERENCES " + ConversationSQL.TABLE_NAME + " (" + ConversationSQL._ID + ") " +
            "ON DELETE RESTRICT " +
            "ON UPDATE RESTRICT," +
            "CONSTRAINT `CMember_userId_constr` " +
            "FOREIGN KEY (" + _USER_ID + ") " +
            "REFERENCES " + UserSQL.TABLE_NAME + " (" + UserSQL._ID + ") " +
            "ON DELETE RESTRICT " +
            "ON UPDATE RESTRICT" +
            ");";

    private static final String ADD = "" +
            "INSERT INTO " + TABLE_NAME + " (" + _CONVERSATION_ID + ", " + _USER_ID + ") VALUES (?, ?);";

    private static final String REMOVE = "" +
            "DELETE FROM " + TABLE_NAME + " WHERE " + _CONVERSATION_ID + "=? AND " + _USER_ID + "=?;";

    private static final String UPDATE = "" +
            "UPDATE " + TABLE_NAME + " SET " + _IS_ADMIN + " = ? WHERE " + _USER_ID + " = ? AND " + _CONVERSATION_ID + " = ?;";

    private static final String CHECK_IF_IN_CONVERSATION = "" + 
            "SELECT " + _USER_ID + " FROM " + TABLE_NAME + " WHERE " + _USER_ID + " = ? AND " + _CONVERSATION_ID + " = ?";
    
    private static final String CHECK_IS_ADMIN = "" + 
            "SELECT " + _USER_ID + " FROM " + TABLE_NAME + " WHERE " + _USER_ID + " = ? AND " + _CONVERSATION_ID + 
            " = ? AND " + _IS_ADMIN + " = true";

    ConversationMemberSQL(ValidConnection connection){
        super(connection);
    }

    void createTable(){
        super.createTable(CREATE_TABLE);
    }

    boolean addToConversation(int userId, int conversationId){
        try (PreparedStatement pstmt = connection.get().prepareStatement(ADD)){
            pstmt.setInt(1, conversationId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        
        return true;
    }

    boolean removeFromConversation(int userId, int conversationId){
        try (PreparedStatement pstmt = connection.get().prepareStatement(REMOVE)){
            pstmt.setInt(1,  conversationId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        
        return true;
    }

    boolean updateIsAdmin(int userId, int conversationId, boolean isAdmin){
        try (PreparedStatement pstmt = connection.get().prepareStatement(UPDATE)){
            pstmt.setBoolean(1,  isAdmin);
            pstmt.setInt(2, userId);
            pstmt.setInt(3,  conversationId);
            pstmt.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        
        return true;
    }

    boolean checkIfInConversation(int userId, int conversationId){
        try (PreparedStatement pstmt = connection.get().prepareStatement(CHECK_IF_IN_CONVERSATION)){
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

    boolean checkIsAdmin(int userId, int conversationId){
        try (PreparedStatement pstmt = connection.get().prepareStatement(CHECK_IS_ADMIN)){
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

}
