package de.thu.inf.spro.chattitude.backend;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySqlClient {
    private Connection mySqlConnection;

    public MySqlClient() {
        connect();
    }

    private void connect() {
        try {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setUser(System.getenv("MYSQL_USER"));
            dataSource.setPassword(System.getenv("MYSQL_PASSWORD"));
            dataSource.setServerName(System.getenv("MYSQL_HOSTNAME"));
            dataSource.setDatabaseName(System.getenv("MYSQL_DATABASE"));
            dataSource.setAutoReconnect(true);

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
            stmt.execute("CREATE TABLE IF NOT EXISTS User (" +
                    "userId INT UNSIGNED AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(32) NOT NULL," +
                    "password VARCHAR(128) NOT NULL," +
                    "registerDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "UNIQUE INDEX `username_UNIQUE` (`username` ASC)" +
                ");");
            stmt.execute("CREATE TABLE IF NOT EXISTS Conversation (" +
                    "`conversationId` INT UNSIGNED NOT NULL PRIMARY KEY," +
                    "`memberOne` INT UNSIGNED NOT NULL," +
                    "`memberTwo` INT UNSIGNED NOT NULL," +
                    "`lastActivity` TIMESTAMP NOT NULL," +
                    "INDEX `user_idx` (`memberOne` ASC, `memberTwo` ASC)," +
                    "CONSTRAINT `memberOne_constr` " +
                        "FOREIGN KEY (`memberOne`) " +
                        "REFERENCES `chattitude`.`User` (`userId`) " +
                        "ON DELETE RESTRICT " +
                        "ON UPDATE RESTRICT," +
                    "CONSTRAINT `memberTwo_constr` " +
                    "FOREIGN KEY (`memberTwo`) " +
                    "REFERENCES `chattitude`.`User` (`userId`) " +
                    "ON DELETE RESTRICT " +
                    "ON UPDATE RESTRICT" +
                ");");
            stmt.execute("CREATE TABLE IF NOT EXISTS ChatMessage (" +
                    "`messageId` INT UNSIGNED NOT NULL PRIMARY KEY," +
                    "`conversationId` INT UNSIGNED NOT NULL," +
                    "`content` TEXT NOT NULL," +
                    "INDEX `conversationId_idx` (`conversationId` ASC)," +
                    "CONSTRAINT `conversationId_constr`" +
                        "FOREIGN KEY (`conversationId`) " +
                        "REFERENCES `chattitude`.`Conversation` (`conversationId`) " +
                        "ON DELETE RESTRICT " +
                        "ON UPDATE RESTRICT " +
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
}
