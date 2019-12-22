package de.thu.inf.spro.chattitude.backend.database;

import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

final class DBUtils {
    
    static Connection connect() throws SQLException {
        MariaDbDataSource dataSource = new MariaDbDataSource();
        dataSource.setUser(System.getenv("MYSQL_USER"));
        dataSource.setPassword(System.getenv("MYSQL_PASSWORD"));
        dataSource.setServerName(System.getenv("MYSQL_HOSTNAME"));
        dataSource.setDatabaseName(System.getenv("MYSQL_DATABASE"));

        return dataSource.getConnection();
    }

    static void dropTables(ValidConnection connection){
        String tableStrings = String.join(", ", UserSQL.TABLE_NAME, ConversationSQL.TABLE_NAME, ConversationMemberSQL.TABLE_NAME,
                FileUploadSQL.TABLE_NAME, MessageSQL.TABLE_NAME);
        
        try(Statement statement = connection.get().createStatement()){
            statement.addBatch("SET FOREIGN_KEY_CHECKS = 0;");
            statement.addBatch(String.format("DROP TABLE IF EXISTS %s;", tableStrings));
            statement.addBatch("SET FOREIGN_KEY_CHECKS = 1;");
            statement.executeBatch();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    
}
