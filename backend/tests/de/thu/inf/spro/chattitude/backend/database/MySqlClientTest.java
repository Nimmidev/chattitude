package de.thu.inf.spro.chattitude.backend.database;

import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.Message;
import de.thu.inf.spro.chattitude.packet.User;
import de.thu.inf.spro.chattitude.packet.packets.ModifyConversationUserPacket;
import de.thu.inf.spro.chattitude.packet.util.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class MySqlClientTest extends SQLTest {

    private static ValidConnection connection;
    private static MySqlClient mySqlClient;
    
    @Before
    public void setup() throws SQLException {
        mySqlClient = new MySqlClient();
        connection = new ValidConnection(MySqlClient.connect(), MySqlClient::connect);
    }
    
    @Test
    public void dropTablesTest() throws SQLException {
        MySqlClient.dropTables(connection);
        Assert.assertEquals(0, getTableCount());
    }
    
    @Test
    public void createTablesTest() throws SQLException {
        MySqlClient.dropTables(connection);
        MySqlClient.createTables(connection, userSQL, conversationSQL, conversationMemberSQL, fileUploadSQL, messageSQL);

        Assert.assertEquals(5, getTableCount());
    }
    
    @Test
    public void connectTest() throws SQLException {
        Connection connection = MySqlClient.connect();
        Assert.assertTrue(connection.isValid(5));
    }
    
    @Test
    public void closeTest() throws SQLException {
        Connection connection = MySqlClient.connect();
        Assert.assertTrue(connection.isValid(5));
        
        connection.close();
        Assert.assertFalse(connection.isValid(5));
    }

    @Test
    public void createConversationGroupTest(){
        String conversationName = "createConversationGroupTest";
        int sessionUserId = userSQL.add("createConversationGroupTest", "qwer");
        final int USER_COUNT = 4;
        User[] users = new User[USER_COUNT];
        
        for(int i = 0; i < USER_COUNT; i++){
            int userId = userSQL.add("createConversationGroupTest" + i, "qwer");
            users[i] = new User(userId, "");
        }
        
        int conversationId = mySqlClient.createConversation(conversationName, sessionUserId, users);
        Assert.assertTrue(conversationId != -1);

        Conversation conversation = conversationSQL.get(conversationId, -1);
        Assert.assertNotNull(conversation);
        Assert.assertEquals(conversationId, conversation.getId());
        Assert.assertEquals(conversationName, conversation.getName());
        Assert.assertEquals(USER_COUNT + 1, conversation.getUsers().length);
    }

    @Test
    public void createConversationGroupWithSessionUserOnlyTest(){
        String conversationName = "createConversationGWSUOTest";
        int sessionUserId = userSQL.add("createConversationGWSUOTest", "qwer");
        int conversationId = mySqlClient.createConversation(conversationName, sessionUserId, new User[]{});
        Assert.assertTrue(conversationId != -1);

        Conversation conversation = conversationSQL.get(conversationId, -1);
        Assert.assertNotNull(conversation);
        Assert.assertEquals(conversationId, conversation.getId());
        Assert.assertEquals(conversationName, conversation.getName());
        Assert.assertEquals(1, conversation.getUsers().length);
        Assert.assertEquals(sessionUserId, conversation.getUsers()[0].getId());
    }

    @Test (expected = IllegalStateException.class)
    public void createConversationSingeChatInvalidStateTest(){
        int sessionUserId = userSQL.add("createConversationSCWSUOTest", "qwer");
        mySqlClient.createConversation(null, sessionUserId, new User[]{});
    }
    
    @Test
    public void createConversationSingleChatTest(){
        String username = "createConvSCTest";
        String sessionUserUsername = username + 1;
        String user2Username = username + 2;
        
        int sessionUserId = userSQL.add(sessionUserUsername, "qwer");
        int userId2 = userSQL.add(user2Username, "qwer");
        User user2 = new User(userId2, "");
        
        int conversationId = mySqlClient.createConversation(null, sessionUserId, new User[]{user2});
        Assert.assertTrue(conversationId != -1);

        Conversation conversation = conversationSQL.get(conversationId, sessionUserId);
        Assert.assertNotNull(conversation);
        Assert.assertEquals(conversationId, conversation.getId());
        Assert.assertEquals(user2Username, conversation.getName());
        Assert.assertEquals(2, conversation.getUsers().length);
    }
    
    @Test
    public void modifyConversationPermissionDeniedTest(){
        boolean success = mySqlClient.modifyConversationUser(ModifyConversationUserPacket.Action.ADD, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        Assert.assertFalse(success);
    }
    
    @Test
    public void modifyConversationUserAddTest(){
        Pair<Integer, Integer> data = createConversationWithSessionUser("modifyConversationUserAddTest1");
        int sessionUserId = data.getKey();
        int conversationId = data.getValue();
        int userId = userSQL.add("modifyConversationUserAddTest2", "qwer");
        
        boolean exists = conversationMemberSQL.checkIfInConversation(userId, conversationId);
        Assert.assertFalse(exists);
        
        boolean success = mySqlClient.modifyConversationUser(ModifyConversationUserPacket.Action.ADD, sessionUserId, userId, conversationId);
        Assert.assertTrue(success);
        
        exists = conversationMemberSQL.checkIfInConversation(userId, conversationId);
        Assert.assertTrue(exists);
    }

    @Test
    public void modifyConversationUserRemoveTest(){
        Pair<Integer, Integer> data = createConversationWithSessionUser("modifyConversationUserRTest1");
        int sessionUserId = data.getKey();
        int conversationId = data.getValue();
        int userId = userSQL.add("modifyConversationUserRTest2", "qwer");
        
        boolean success = conversationMemberSQL.addToConversation(userId, conversationId);
        Assert.assertTrue(success);
        
        boolean exists = conversationMemberSQL.checkIfInConversation(userId, conversationId);
        Assert.assertTrue(exists);
        
        success = mySqlClient.modifyConversationUser(ModifyConversationUserPacket.Action.REMOVE, sessionUserId, userId, conversationId);
        Assert.assertTrue(success);

        exists = conversationMemberSQL.checkIfInConversation(userId, conversationId);
        Assert.assertFalse(exists);
    }

    @Test
    public void modifyConversationUserPromoteAdminTest(){
        Pair<Integer, Integer> data = createConversationWithSessionUser("modifyConversationUserPATest1");
        int sessionUserId = data.getKey();
        int conversationId = data.getValue();
        int userId = userSQL.add("modifyConversationUserPATest2", "qwer");

        boolean success = conversationMemberSQL.addToConversation(userId, conversationId);
        Assert.assertTrue(success);

        boolean isAdmin = conversationMemberSQL.checkIsAdmin(userId, conversationId);
        Assert.assertFalse(isAdmin);

        success = mySqlClient.modifyConversationUser(ModifyConversationUserPacket.Action.PROMOTE_ADMIN, sessionUserId, userId, conversationId);
        Assert.assertTrue(success);

        isAdmin = conversationMemberSQL.checkIsAdmin(userId, conversationId);
        Assert.assertTrue(isAdmin);
    }

    @Test
    public void modifyConversationUserDemoteAdminTest(){
        Pair<Integer, Integer> data = createConversationWithSessionUser("modifyConversationUserDATest1");
        int sessionUserId = data.getKey();
        int conversationId = data.getValue();

        boolean isAdmin = conversationMemberSQL.checkIsAdmin(sessionUserId, conversationId);
        Assert.assertTrue(isAdmin);

        boolean success = mySqlClient.modifyConversationUser(ModifyConversationUserPacket.Action.DEMOTE_ADMIN, sessionUserId, sessionUserId, conversationId);
        Assert.assertTrue(success);

        isAdmin = conversationMemberSQL.checkIsAdmin(sessionUserId, conversationId);
        Assert.assertFalse(isAdmin);
    }

    @Test
    public void saveMessageTest(){
        int sessionUserId = mySqlClient.addUser("saveMessageTest1", "qwer");
        int userId = mySqlClient.addUser("saveMessageTest2", "qwer");
        User user = new User(userId, "saveMessageTest2");
        int conversationId = mySqlClient.createConversation(null, sessionUserId, new User[]{user});
        Message message = new Message(conversationId, "test", user);
        int messageId = mySqlClient.saveMessage(sessionUserId, message);
        Assert.assertNotEquals(-1, messageId);
    }

    @Test
    public void saveMessageFailTest(){
        int sessionUserId = mySqlClient.addUser("saveMessageFailTest1", "qwer");
        int userId = mySqlClient.addUser("saveMessageFailTest2", "qwer");
        User user = new User(userId, "saveMessageFailTest2");
        int conversationId = mySqlClient.createConversation("saveMessageFailTest", sessionUserId, new User[]{});
        Message message = new Message(conversationId, "test", user);
        int messageId = mySqlClient.saveMessage(userId, message);
        Assert.assertEquals(-1, messageId);
    }

    @Test
    public void getMessageHistoryTest(){
        int sessionUserId = mySqlClient.addUser("getMessHisTest", "qwer");
        User user = new User(sessionUserId, "getMessHisTest2");
        int conversationId = mySqlClient.createConversation("test", sessionUserId, new User[]{});
        int lastMessageId = -1;
        final int MESSAGE_COUNT = 4;

        for(int i = 0; i < MESSAGE_COUNT; i++){
            Message message = new Message(conversationId, "test", user);
            lastMessageId = mySqlClient.saveMessage(sessionUserId, message);
        }

        List<Message> messages = mySqlClient.getMessageHistory(sessionUserId, conversationId, lastMessageId);
        Assert.assertEquals(MESSAGE_COUNT - 1, messages.size());
    }

    @Test
    public void getMessageHistoryFailTest(){
        int sessionUserId = mySqlClient.addUser("getMessHisFailTest1", "qwer");
        int userId = mySqlClient.addUser("getMessHisFailTest2", "qwer");
        User user = new User(userId, "getMessHisFailTest2");
        int conversationId = mySqlClient.createConversation("getMessHisFailTest", userId, new User[]{});
        int lastMessageId = -1;
        final int MESSAGE_COUNT = 4;

        for(int i = 0; i < MESSAGE_COUNT; i++){
            Message message = new Message(conversationId, "test", user);
            lastMessageId = mySqlClient.saveMessage(sessionUserId, message);
        }

        List<Message> messages = mySqlClient.getMessageHistory(sessionUserId, conversationId, lastMessageId);
        Assert.assertNull(messages);
    }

    private Pair<Integer, Integer> createConversationWithSessionUser(String name){
        int userId = userSQL.add(name, "qwer");
        int conversationId = conversationSQL.add(name);
        
        boolean success = conversationMemberSQL.addToConversation(userId, conversationId);
        Assert.assertTrue(success);
        
        success = conversationMemberSQL.updateIsAdmin(userId, conversationId, true);
        Assert.assertTrue(success);
        
        return new Pair<>(userId, conversationId);
    }
    
    private int getTableCount() throws SQLException {
        int tableCount = 0;
        Statement statement = connection.get().createStatement();
        statement.execute("SHOW TABLES");

        if(statement.getResultSet() != null){
            while(statement.getResultSet().next()) tableCount++;
        }
        
        return tableCount;
    }
    
}
