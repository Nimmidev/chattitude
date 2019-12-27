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

        boolean success = conversationMemberSQL.addToConversation(userId, conversationId);
        conversations = conversationSQL.getUserConversations(userId);
        Assert.assertTrue(success);
        Assert.assertEquals(1, conversations.size());
    }

    @Test
    // should throw SQLIntegrityConstraintViolationException: Duplicate entry for key PRIMARY
    public void addToConversationAlreadyExistsTest(){
        int userId = userSQL.add("addToConvAlExTe", "qwer");
        int conversationId = conversationSQL.add("addToConvAlExTe");

        boolean success = conversationMemberSQL.addToConversation(userId, conversationId);
        Assert.assertTrue(success);
        
        success = conversationMemberSQL.addToConversation(userId, conversationId);
        Assert.assertFalse(success);
    }
    
    @Test
    public void removeFromConversationTest(){
        int userId = userSQL.add("removeFromConversationTest", "qwer");
        int conversationId = conversationSQL.add("removeFromConversationTest");

        boolean success = conversationMemberSQL.addToConversation(userId, conversationId);
        Assert.assertTrue(success);
        
        List<Conversation> conversations = conversationSQL.getUserConversations(userId);
        Assert.assertEquals(1, conversations.size());

        success = conversationMemberSQL.removeFromConversation(userId, conversationId);
        Assert.assertTrue(success);
        
        conversations = conversationSQL.getUserConversations(userId);
        Assert.assertEquals(0, conversations.size());
    }
    
    @Test
    public void checkIsAdminTest(){
        int userId = userSQL.add("checkIsAdminTest", "qwer");
        int conversationId = conversationSQL.add("checkIsAdminTest");
        
        boolean success = conversationMemberSQL.addToConversation(userId, conversationId);
        Assert.assertTrue(success);
        
        boolean isAdmin = conversationMemberSQL.checkIsAdmin(userId, conversationId);
        Assert.assertFalse(isAdmin);
    }
    
    @Test
    public void updateIsAdminTest(){
        int userId = userSQL.add("updateIsAdminTest", "qwer");
        int conversationId = conversationSQL.add("updateIsAdminTest");

        boolean success = conversationMemberSQL.addToConversation(userId, conversationId);
        Assert.assertTrue(success);
        
        boolean isAdmin = conversationMemberSQL.checkIsAdmin(userId, conversationId);
        Assert.assertFalse(isAdmin);

        success = conversationMemberSQL.updateIsAdmin(userId, conversationId, true);
        Assert.assertTrue(success);
        
        isAdmin = conversationMemberSQL.checkIsAdmin(userId, conversationId);
        Assert.assertTrue(isAdmin);
        
        success = conversationMemberSQL.updateIsAdmin(userId, conversationId, false);
        Assert.assertTrue(success);

        isAdmin = conversationMemberSQL.checkIsAdmin(userId, conversationId);
        Assert.assertFalse(isAdmin);
    }
    
    @Test
    public void checkIfInConversationTest(){
        int userId = userSQL.add("checkIfInConversationTest", "qwer");
        int conversationId1 = conversationSQL.add("checkIfInConversationTest1");
        int conversationId2 = conversationSQL.add("checkIfInConversationTest2");
        
        boolean inConversation = conversationMemberSQL.checkIfInConversation(userId, conversationId1);
        Assert.assertFalse(inConversation);
        
        boolean success = conversationMemberSQL.addToConversation(userId, conversationId1);
        Assert.assertTrue(success);

        inConversation = conversationMemberSQL.checkIfInConversation(userId, conversationId1);
        Assert.assertTrue(inConversation);

        inConversation = conversationMemberSQL.checkIfInConversation(userId, conversationId2);
        Assert.assertFalse(inConversation);
    }
    
}
