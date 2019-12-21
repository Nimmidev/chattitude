package de.thu.inf.spro.chattitude.backend.database;

import java.sql.SQLException;
import java.sql.Statement;

abstract class BaseSQL {

    protected ValidConnection connection;

    BaseSQL(ValidConnection connection){
        this.connection = connection;
    }

    void createTable(String query){
        try(Statement statement = connection.get().createStatement()){
            statement.execute(query);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

}
