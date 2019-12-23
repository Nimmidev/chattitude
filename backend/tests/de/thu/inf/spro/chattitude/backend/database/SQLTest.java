package de.thu.inf.spro.chattitude.backend.database;

import org.junit.BeforeClass;

import java.sql.Connection;
import java.sql.SQLException;

abstract class SQLTest {
    
    static UserSQL userSQL;
    static ConversationSQL conversationSQL;
    static ConversationMemberSQL conversationMemberSQL;
    static FileUploadSQL fileUploadSQL;
    static MessageSQL messageSQL;

    @BeforeClass
    public static void createDBConnection() throws SQLException {
        Connection mysqlConnection = MySqlClient.connect();
        ValidConnection connection = new ValidConnection(mysqlConnection, MySqlClient::connect);

        userSQL = new UserSQL(connection);
        conversationSQL = new ConversationSQL(connection);
        conversationMemberSQL = new ConversationMemberSQL(connection);
        fileUploadSQL = new FileUploadSQL(connection);
        messageSQL = new MessageSQL(connection, conversationSQL, fileUploadSQL);

        //Reset database
        MySqlClient.dropTables(connection);
        MySqlClient.createTables(connection, userSQL, conversationSQL, conversationMemberSQL, fileUploadSQL, messageSQL);
    }
    
}
