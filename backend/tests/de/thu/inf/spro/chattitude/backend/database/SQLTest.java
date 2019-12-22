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
    public static void createDBConnection(){
        try {
            Connection mysqlConnection = DBUtils.connect();
            ValidConnection connection = new ValidConnection(mysqlConnection, DBUtils::connect);

            userSQL = new UserSQL(connection);
            conversationSQL = new ConversationSQL(connection);
            conversationMemberSQL = new ConversationMemberSQL(connection);
            fileUploadSQL = new FileUploadSQL(connection);
            messageSQL = new MessageSQL(connection, conversationSQL, fileUploadSQL);

            //Reset database
            DBUtils.dropTables(connection);

            userSQL.createTable();
            conversationSQL.createTable();
            conversationMemberSQL.createTable();
            fileUploadSQL.createTable();
            messageSQL.createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
