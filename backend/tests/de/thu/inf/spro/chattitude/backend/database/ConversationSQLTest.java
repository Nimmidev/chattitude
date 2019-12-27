package de.thu.inf.spro.chattitude.backend.database;

import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.Message;
import de.thu.inf.spro.chattitude.packet.User;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

public class ConversationSQLTest extends SQLTest {
    
    @Test 
    public void addConversationTest(){
        conversationSQL.add("addConversationTest");
    }
    
    @Test 
    public void getConversationTest(){
        String name = "getConversationTest";
        int conversationId = conversationSQL.add(name);
        Conversation conversation = conversationSQL.get(conversationId);
        
        Assert.assertNotNull(conversation);
        Assert.assertEquals(name, conversation.getName());
        Assert.assertEquals(conversationId, conversation.getId());
    }

    @Test
    public void getConversationNonExistentTest(){
        Conversation conversation = conversationSQL.get(Integer.MAX_VALUE);
        Assert.assertNull(conversation);
    }

    @Test
    public void getConversationUsersTest(){
        int conversationId = conversationSQL.add("getConversationUsersTest");
        final int USER_COUNT = 5;

        for(int i = 0; i < USER_COUNT; i++){
            int userId = userSQL.add("getConversationUsersTestUser" + i, "qwer");
            boolean success = conversationMemberSQL.addToConversation(userId, conversationId);
            Assert.assertTrue(success);
        }

        List<User> users = conversationSQL.getUsers(conversationId);
        Assert.assertEquals(USER_COUNT, users.size());
    }

    @Test
    public void getConversationUsersEmptyTest(){
        int conversationId = conversationSQL.add("getConversationUsersEmptyTest");

        List<User> users = conversationSQL.getUsers(conversationId);
        Assert.assertEquals(0, users.size());
    }

    @Test
    public void getUserConversationsTest(){
        int userId =  userSQL.add("getUserConversationsTest", "qwer");
        final int CONVERSATION_COUNT = 3;

        for(int i = 0; i < CONVERSATION_COUNT; i++){
            int conversationId = conversationSQL.add("getUserConversationsTest" + i);
            conversationMemberSQL.addToConversation(userId, conversationId);
        }

        List<Conversation> conversations = conversationSQL.getUserConversations(userId);
        Assert.assertEquals(CONVERSATION_COUNT, conversations.size());
    }

    @Test
    public void getUserConversationsNoneTest(){
        int userId =  userSQL.add("getUserConversationsNoneTest", "qwer");
        
        List<Conversation> conversations = conversationSQL.getUserConversations(userId);
        Assert.assertEquals(0, conversations.size());
    }

    @Test
    public void updateLastMessageIdTest(){
        int conversationId = conversationSQL.add("updateLastMessageIdTest");

        int userId =  userSQL.add("updateLastMessageIdTest", "qwer");
        User user = new User(userId, "test");

        Message message = new Message(conversationId, "Test", user);
        messageSQL.add(message);
        int messageId = messageSQL.getLastInsertId();

        boolean success = conversationSQL.updateLastMessageId(conversationId, messageId);
        Assert.assertTrue(success);
        
        Conversation conversation = conversationSQL.get(conversationId);
        Assert.assertNotNull(conversation);
        Assert.assertEquals(messageId, conversation.getMessage().getId());
    }

    @Test
    // should throw SQLIntegrityConstraintViolationException: foreign key constraint fails
    public void updateLastMessageIdNonExistentTest(){
        int conversationId = conversationSQL.add("updateLastMessageIdNonExistentTest");
        boolean success = conversationSQL.updateLastMessageId(conversationId, Integer.MAX_VALUE);
        Assert.assertFalse(success);
    }
    
}
