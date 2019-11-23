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
            var metaData = mySqlConnection.getMetaData();
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
                    "`conversationId` INT UNSIGNED NOT NULL PRIMARY KEY," +
                    "`lastActivity` TIMESTAMP NOT NULL" +
                    ",`lastMessageId` INT UNSIGNED" +
                ");");
            stmt.execute("CREATE TABLE IF NOT EXISTS ChatMessage (" +
                    "`messageId` INT UNSIGNED NOT NULL PRIMARY KEY," +
                    "`conversationId` INT UNSIGNED NOT NULL," +
                    "`content` TEXT NOT NULL," +
                    "`sender` INT UNSIGNED NOT NULL, " +
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

            stmt.execute("CREATE TABLE `FileUpload` ( " +
                    "`fileId` BINARY(252) NOT NULL PRIMARY KEY UNIQUE , " +
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
}
