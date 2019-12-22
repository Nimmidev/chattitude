package de.thu.inf.spro.chattitude.backend.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

abstract class BaseSQL {

    protected ValidConnection connection;

    BaseSQL(ValidConnection connection){
        this.connection = connection;
    }

    void createTable(String query){
        try(Statement statement = connection.get().createStatement()){
            statement.executeUpdate(query);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    
    int getLastInsertId(){
        String query = "SELECT LAST_INSERT_ID();";

        try (Statement statement = connection.get().createStatement()){
            statement.execute(query);

            if(statement.getResultSet() != null && statement.getResultSet().next()){
                return statement.getResultSet().getInt("LAST_INSERT_ID()");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return -1;
    }

}
