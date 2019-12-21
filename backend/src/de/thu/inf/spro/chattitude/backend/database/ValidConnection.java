package de.thu.inf.spro.chattitude.backend.database;

import java.sql.Connection;
import java.sql.SQLException;

final class ValidConnection {

    interface RenewCallback {
        Connection renew() throws SQLException;
    }

    private Connection connection;
    private RenewCallback callback;

    ValidConnection(Connection connection, RenewCallback callback){
        this.connection = connection;
        this.callback = callback;
    }

    Connection get(){
        try {
            if(!connection.isValid(5)){
                connection = callback.renew();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

}
