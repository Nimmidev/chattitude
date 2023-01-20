package de.thu.inf.spro.chattitude.backend;

import de.thu.inf.spro.chattitude.backend.database.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.sql.SQLException;

@RunWith(Suite.class)
@Suite.SuiteClasses({ConversationMemberSQLTest.class, ConversationSQLTest.class, FileUploadSQLTest.class,
        MessageSQLTest.class,  MySqlClientTest.class, UserSQLTest.class, AuthenticationManagerTest.class, ServerTest.class})
public class BackendTestSuite {

    @BeforeClass
    public static void setup() throws SQLException {
         SQLTest.resetDatabase();
    }

    @AfterClass
    public static void cleanUp() throws SQLException {
        SQLTest.resetDatabase();
    }

}
