package de.thu.inf.spro.chattitude.backend.database;

import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.Message;
import de.thu.inf.spro.chattitude.packet.User;
import org.junit.Assert;
import org.junit.Test;

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
        
        Assert.assertEquals(name, conversation.getName());
        Assert.assertEquals(conversationId, conversation.getId());
    }
    
    @Test
    public void getConversationUsersTest(){
        int conversationId = conversationSQL.add("getConversationUsersTest");
        final int USER_COUNT = 5;
        
        for(int i = 0; i < USER_COUNT; i++){
            int userId = userSQL.add("getConversationUsersTestUser" + i, "qwer");
            conversationMemberSQL.addToConversation(userId, conversationId);
            System.out.println(String.format("Added user %d to conversation %d", userId, conversationId));
        }
        
        List<User> users = conversationSQL.getUsers(conversationId);
        Assert.assertEquals(USER_COUNT, users.size());
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
    public void updateLastMessageIdTest(){
        int conversationId = conversationSQL.add("updateLastMessageIdTest");
        
        int userId =  userSQL.add("updateLastMessageIdTest", "qwer");
        User user = new User(userId, "test");
        
        Message message = new Message(conversationId, "Test", user);
        messageSQL.add(message);
        int messageId = messageSQL.getLastInsertId();
        
        conversationSQL.updateLastMessageId(conversationId, messageId);
        Conversation conversation = conversationSQL.get(conversationId);
        Assert.assertEquals(messageId, conversation.getMessage().getId());
    }
    
}
