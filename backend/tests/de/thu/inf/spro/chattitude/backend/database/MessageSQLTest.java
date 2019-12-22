package de.thu.inf.spro.chattitude.backend.database;

import de.thu.inf.spro.chattitude.packet.Message;
import de.thu.inf.spro.chattitude.packet.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class MessageSQLTest extends SQLTest {
    
    @Test
    public void addMessageTest(){
        int conversationId = conversationSQL.add("addMessageTest");
        int userId = userSQL.add("addMessageTest", "qwer");
        User user = new User(userId, "addMessageTest");
        Message message = new Message(conversationId, "test", user);
        messageSQL.add(message);
    }
    
    @Test
    public void getMessageHistoryTest(){
        int conversationId = conversationSQL.add("getMessageHistoryTest");
        int userId = userSQL.add("getMessageHistoryTest", "qwer");
        User user = new User(userId, "getMessageHistoryTest");
        final int MESSAGE_COUNT = 4;
        
        List<Message> messages = messageSQL.getHistory(conversationId, Integer.MAX_VALUE);
        Assert.assertEquals(0, messages.size());
        
        for(int i = 0; i < MESSAGE_COUNT; i++){
            Message message = new Message(conversationId, "test" + i, user);
            messageSQL.add(message);
        }

        messages = messageSQL.getHistory(conversationId, Integer.MAX_VALUE);
        Assert.assertEquals(MESSAGE_COUNT, messages.size());
    }
    
}
