package de.thu.inf.spro.chattitude.backend.database;

import de.thu.inf.spro.chattitude.packet.Conversation;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ConversationMemberSQLTest extends SQLTest {
    
    @Test
    public void addToConversationTest(){
        int userId = userSQL.add("addToConversationTest", "qwer");
        int conversationId = conversationSQL.add("addToConversationTest");
        
        List<Conversation> conversations = conversationSQL.getUserConversations(userId);
        Assert.assertEquals(0, conversations.size());
        
        conversationMemberSQL.addToConversation(userId, conversationId);
        conversations = conversationSQL.getUserConversations(userId);
        Assert.assertEquals(1, conversations.size());
    }
    
    @Test
    public void removeFromConversationTest(){
        int userId = userSQL.add("removeFromConversationTest", "qwer");
        int conversationId = conversationSQL.add("removeFromConversationTest");
        
        conversationMemberSQL.addToConversation(userId, conversationId);
        List<Conversation> conversations = conversationSQL.getUserConversations(userId);
        Assert.assertEquals(1, conversations.size());

        conversationMemberSQL.removeFromConversation(userId, conversationId);
        conversations = conversationSQL.getUserConversations(userId);
        Assert.assertEquals(0, conversations.size());
    }
    
    @Test
    public void checkIsAdminTest(){
        int userId = userSQL.add("checkIsAdminTest", "qwer");
        int conversationId = conversationSQL.add("checkIsAdminTest");
        
        conversationMemberSQL.addToConversation(userId, conversationId);
        boolean isAdmin = conversationMemberSQL.checkIsAdmin(userId, conversationId);
        Assert.assertFalse(isAdmin);
    }
    
    @Test
    public void updateIsAdminTest(){
        int userId = userSQL.add("updateIsAdminTest", "qwer");
        int conversationId = conversationSQL.add("updateIsAdminTest");

        conversationMemberSQL.addToConversation(userId, conversationId);
        boolean isAdmin = conversationMemberSQL.checkIsAdmin(userId, conversationId);
        Assert.assertFalse(isAdmin);

        conversationMemberSQL.updateIsAdmin(userId, conversationId, true);
        isAdmin = conversationMemberSQL.checkIsAdmin(userId, conversationId);
        Assert.assertTrue(isAdmin);
    }
    
    @Test
    public void checkIfInConversationTest(){
        int userId = userSQL.add("checkIfInConversationTest", "qwer");
        int conversationId = conversationSQL.add("checkIfInConversationTest");
        
        boolean inConversation = conversationMemberSQL.checkIfInConversation(userId, conversationId);
        Assert.assertFalse(inConversation);
        
        conversationMemberSQL.addToConversation(userId, conversationId);
        inConversation = conversationMemberSQL.checkIfInConversation(userId, conversationId);
        Assert.assertTrue(inConversation);
    }
    
}
