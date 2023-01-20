package de.thu.inf.spro.chattitude.backend.database;

import de.thu.inf.spro.chattitude.packet.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

final class UserSQL extends BaseSQL {

    static final String TABLE_NAME = "User";
    static final String _ID = "userId";
    static final String _USERNAME = "username";
    static final String _PASSWORD = "password";
    static final String _REGISTER_DATE = "registerDate";

    private static final String CREATE_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            _ID + " INT UNSIGNED AUTO_INCREMENT PRIMARY KEY," +
            _USERNAME + " VARCHAR(32) NOT NULL," +
            _PASSWORD + " VARCHAR(128) NOT NULL," +
            _REGISTER_DATE + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
            "UNIQUE INDEX username_UNIQUE (" + _USERNAME + " ASC)" +
            ");";

    private static final String ADD = "" + 
            "INSERT INTO " + TABLE_NAME + " (" + _USERNAME + ", " + _PASSWORD + ") VALUES (?,?);";

    private static final String GET_ID_BY_USERNAME = "" +
            "SELECT " + _ID + " FROM " + TABLE_NAME + " WHERE " + _USERNAME + "=?;";

    private static final String GET_USERNAME_BY_ID = "" +
            "SELECT " + _USERNAME + " FROM " + TABLE_NAME + " WHERE " + _ID + "=?;";

    private static final String GET_BY_NAME = "" + 
            "SELECT " + _ID + ", " + _USERNAME + " FROM " + TABLE_NAME + " WHERE " + _USERNAME + " LIKE ?";

    private static final String CHECK_EXISTENCE_BY_USERNAME = "" +
            "Select " + _USERNAME + " FROM " + TABLE_NAME + " WHERE " + _USERNAME + " = ?;";
    
    private static final String CHECK_EXISTENCE_BY_USER_ID = "" +
            "Select " + _USERNAME + " FROM " + TABLE_NAME + " WHERE " + _ID + " = ?;";

    private static final String CHECK_CREDENTIALS = "" + 
            "Select " + _USERNAME + " FROM " + TABLE_NAME + " WHERE " + _USERNAME + "=? AND " + _PASSWORD + "=?;";

    UserSQL(ValidConnection connection){
        super(connection);
    }

    void createTable(){
        super.createTable(CREATE_TABLE);
    }

    int getId(String username){
        int userId = -1;

        try (PreparedStatement pstmt = connection.get().prepareStatement(GET_ID_BY_USERNAME)){
            pstmt.setString(1, username);
            pstmt.execute();
            if(pstmt.getResultSet().next()){
                userId = pstmt.getResultSet().getInt(_ID);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return userId;
    }

    String getUsername(int id){
        String username = "";

        try (PreparedStatement pstmt = connection.get().prepareStatement(GET_USERNAME_BY_ID)){
            pstmt.setInt(1, id);
            pstmt.execute();
            if(pstmt.getResultSet().next()){
                username = pstmt.getResultSet().getString(_USERNAME);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return username;
    }

    List<User> search(String searchQuery){
        try (PreparedStatement pstmt = connection.get().prepareStatement(GET_BY_NAME)){
            pstmt.setString(1, "%" + searchQuery + "%");
            pstmt.execute();

            return getSearchResults(pstmt.getResultSet());
        } catch (SQLException e){
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private List<User> getSearchResults(ResultSet resultSet) throws SQLException {
        List<User> users = new ArrayList<>();

        while(resultSet.next()){
            int userId = resultSet.getInt(_ID);
            String username = resultSet.getString(_USERNAME);
            users.add(new User(userId, username));
        }

        return users;
    }

    int add(String username, String password){
        int userId = -1;

        try {
            connection.get().setAutoCommit(false);

            try (PreparedStatement pstmt = connection.get().prepareStatement(ADD)){
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.execute();

                userId = getLastInsertId();
            } catch (SQLException e){
                connection.get().rollback();
                e.printStackTrace();
            } finally {
                connection.get().setAutoCommit(true);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }

        return userId;
    }

    boolean checkExistence(String username){
        boolean result = false;

        try (PreparedStatement pstmt = connection.get().prepareStatement(CHECK_EXISTENCE_BY_USERNAME)){
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

    boolean checkExistence(int userId){
        boolean result = false;

        try (PreparedStatement pstmt = connection.get().prepareStatement(CHECK_EXISTENCE_BY_USER_ID)){
            pstmt.setInt(1, userId);
            pstmt.execute();

            if(pstmt.getResultSet() != null){
                result = pstmt.getResultSet().next();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return result;
    }

    boolean checkCredentials(String username, String password){
        boolean result = false;

        try (PreparedStatement pstmt = connection.get().prepareStatement(CHECK_CREDENTIALS)){
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
