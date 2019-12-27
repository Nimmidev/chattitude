package de.thu.inf.spro.chattitude.backend;

import de.thu.inf.spro.chattitude.backend.database.MySqlClient;
import de.thu.inf.spro.chattitude.packet.Credentials;
import org.java_websocket.WebSocket;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class AuthenticationManagerTest {
    
    private static MySqlClient mySqlClient;
    private static AuthenticationManager authenticationManager;
    
    @BeforeClass
    public static void setup(){
        mySqlClient = new MySqlClient();
        authenticationManager = new AuthenticationManager(mySqlClient);
    }

    @AfterClass
    public static void cleanUp(){
        mySqlClient.close();
    }

    @Test
    public void registerTest(){
        String username = "registerTest";
        int userId = mySqlClient.getUserId(username);
        Assert.assertEquals(-1, userId);

        Credentials credentials = new Credentials(username, "qwer");
        WebSocket webSocket = new MockWebSocket();
        boolean success = authenticationManager.register(credentials, webSocket);
        Assert.assertTrue(success);
        Assert.assertNotNull(webSocket.getAttachment());

        userId = mySqlClient.getUserId(username);
        credentials = webSocket.getAttachment();
        Assert.assertEquals(userId, credentials.getUserId());
        Assert.assertEquals(username, credentials.getUsername());
        Assert.assertTrue(credentials.isAuthenticated());
    }

    @Test
    public void registerFailTest(){
        Credentials credentials = new Credentials("registerFailTest", "qwer");
        WebSocket webSocket = new MockWebSocket();
        
        boolean success = authenticationManager.register(credentials, webSocket);
        Assert.assertTrue(success);

        success = authenticationManager.register(credentials, webSocket);
        Assert.assertFalse(success);
    }

    @Test
    public void authenticateTest(){
        Credentials credentials = new Credentials("authenticateTest", "qwer");
        WebSocket webSocket = new MockWebSocket();
        
        boolean success = authenticationManager.register(credentials, webSocket);
        Assert.assertTrue(success);
        
        success = authenticationManager.authenticate(credentials, webSocket);
        credentials = webSocket.getAttachment();
        
        Assert.assertTrue(success);
        Assert.assertNotNull(credentials);
        Assert.assertTrue(credentials.isAuthenticated());
    }
    
    @Test
    public void authenticateFailTest(){
        Credentials credentials = new Credentials("authenticateFailTest", "qwer");
        WebSocket webSocket = new MockWebSocket();
        
        boolean success = authenticationManager.authenticate(credentials, webSocket);
        credentials = webSocket.getAttachment();
        
        Assert.assertFalse(success);
        Assert.assertNotNull(credentials);
        Assert.assertFalse(credentials.isAuthenticated());
    }
    
}
