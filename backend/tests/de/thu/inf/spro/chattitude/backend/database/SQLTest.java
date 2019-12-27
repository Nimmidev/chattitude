package de.thu.inf.spro.chattitude.backend.database;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class SQLTest {
    
    static UserSQL userSQL;
    static ConversationSQL conversationSQL;
    static ConversationMemberSQL conversationMemberSQL;
    static FileUploadSQL fileUploadSQL;
    static MessageSQL messageSQL;

    private static ValidConnection connection;

    @BeforeClass
    public static void setupSQLObjects() throws SQLException {
        Connection mysqlConnection = MySqlClient.connect();
        connection = new ValidConnection(mysqlConnection, MySqlClient::connect);

        userSQL = new UserSQL(connection);
        conversationSQL = new ConversationSQL(connection);
        conversationMemberSQL = new ConversationMemberSQL(connection);
        fileUploadSQL = new FileUploadSQL(connection);
        messageSQL = new MessageSQL(connection, conversationSQL, fileUploadSQL);
    }

    public static void resetDatabase() throws SQLException {
        if(connection == null) setupSQLObjects();
        MySqlClient.dropTables(connection);
        MySqlClient.createTables(connection, userSQL, conversationSQL, conversationMemberSQL, fileUploadSQL, messageSQL);
    }
    
}
