package de.thu.inf.spro.chattitude.backend;

import de.thu.inf.spro.chattitude.packet.Credentials;
import de.thu.inf.spro.chattitude.packet.PacketType;
import de.thu.inf.spro.chattitude.packet.User;
import de.thu.inf.spro.chattitude.packet.packets.AuthenticationPacket;
import de.thu.inf.spro.chattitude.packet.packets.Packet;
import de.thu.inf.spro.chattitude.packet.packets.RegisterPacket;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class ServerTest {

    private static Server server;

    @BeforeClass
    public static void setup(){
        server = new Server();
    }

    @AfterClass
    public static void cleanUp(){
        server.close();
    }

    @Test
    public void onRegisterTest(){
        MockWebSocket webSocket = new MockWebSocket();
        String username = "onRegisterTest";
        String password = "onRegisterTest";

        webSocket.setOnMessageCallback(packet -> {
            RegisterPacket registerPacket = (RegisterPacket) packet;
            Assert.assertTrue(registerPacket.wasSuccessful());
            Assert.assertTrue(registerPacket.getCredentials().isAuthenticated());
            Assert.assertEquals(username, registerPacket.getCredentials().getUsername());
            Assert.assertEquals(password, registerPacket.getCredentials().getPassword());
            return null;
        });

        server.onRegister(new RegisterPacket(new Credentials(username, password)), webSocket);
    }

    @Test
    public void onRegisterFailTest(){
        MockWebSocket webSocket = new MockWebSocket();
        String username = "onRegisterFailTest";
        String password = "onRegisterFailTest";

        server.onRegister(new RegisterPacket(new Credentials(username, password)), webSocket);
        webSocket.setOnMessageCallback(packet -> {
            RegisterPacket registerPacket = (RegisterPacket) packet;
            Assert.assertFalse(registerPacket.wasSuccessful());
            Assert.assertFalse(registerPacket.getCredentials().isAuthenticated());
            Assert.assertEquals(username, registerPacket.getCredentials().getUsername());
            Assert.assertEquals(password, registerPacket.getCredentials().getPassword());
            return null;
        });

        server.onRegister(new RegisterPacket(new Credentials(username, password)), webSocket);
    }

    @Test
    public void authenticationTest(){
        MockWebSocket webSocket = new MockWebSocket();
        String username = "authenticationTest";
        String password = "authenticationTest";

        server.onRegister(new RegisterPacket(new Credentials(username, password)), webSocket);
        webSocket.setOnMessageCallback(packet -> {
            AuthenticationPacket authenticationPacket = (AuthenticationPacket) packet;
            Assert.assertTrue(authenticationPacket.wasSuccessful());
            Assert.assertTrue(authenticationPacket.getCredentials().isAuthenticated());
            Assert.assertEquals(username, authenticationPacket.getCredentials().getUsername());
            Assert.assertEquals(password, authenticationPacket.getCredentials().getPassword());
            return null;
        });

        server.onAuthenticate(new AuthenticationPacket(new Credentials(username, password)), webSocket);
    }

    @Test
    public void authenticationFailTest(){
        MockWebSocket webSocket = new MockWebSocket();
        String username = "authenticationFailTest";
        String password = "authenticationFailTest";

        webSocket.setOnMessageCallback(packet -> {
            AuthenticationPacket authenticationPacket = (AuthenticationPacket) packet;
            Assert.assertFalse(authenticationPacket.wasSuccessful());
            Assert.assertFalse(authenticationPacket.getCredentials().isAuthenticated());
            Assert.assertEquals(username, authenticationPacket.getCredentials().getUsername());
            Assert.assertEquals(password, authenticationPacket.getCredentials().getPassword());
            return null;
        });

        server.onAuthenticate(new AuthenticationPacket(new Credentials(username, password)), webSocket);
    }

    @Test
    public void broadcastPacketTest(){
        final int USER_COUNT = 6;
        User[] users = new User[USER_COUNT];
        AtomicInteger packetsReceived = new AtomicInteger();
        MockWebSocket webSocket = new MockWebSocket();

        webSocket.setOnMessageCallback(packet -> {
            if(packet.getType() == PacketType.CONNECTED) packetsReceived.getAndIncrement();
            return null;
        });

        for(int i = 0; i < USER_COUNT; i++){
            RegisterPacket packet = new RegisterPacket(new Credentials("broadcastPacketTest"+i, "qwer"));
            server.onRegister(packet, webSocket);
            users[i] = new User(packet.getCredentials().getUserId(), packet.getCredentials().getUsername());
        }
        server.broadcastPacket(users, new Packet(PacketType.CONNECTED));

        Assert.assertEquals(USER_COUNT, packetsReceived.get());
    }

}
