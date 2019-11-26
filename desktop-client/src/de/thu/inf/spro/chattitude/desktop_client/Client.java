package de.thu.inf.spro.chattitude.desktop_client;

import de.thu.inf.spro.chattitude.desktop_client.network.WebSocketClient;
import de.thu.inf.spro.chattitude.desktop_client.ui.Window;
import de.thu.inf.spro.chattitude.packet.*;
import de.thu.inf.spro.chattitude.packet.packets.*;
import javafx.application.Application;
import org.java_websocket.WebSocket;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

class Client implements PacketHandler {

    private Window window;
    private WebSocketClient webSocketClient;

    private Credentials testCredientials = new Credentials("Nimmi", "qwer");

    public Client() throws MalformedURLException, URISyntaxException {
        window = new Window();

        webSocketClient = new WebSocketClient(this, 8080);
        webSocketClient.connect();

        window.setOnCloseListener(() -> {
            try {
                webSocketClient.closeBlocking();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Application.launch(Window.class);
    }

    @Override
    public void onConnected(WebSocket webSocket) {
        
        System.out.println("ConnectedPacket");
        User user = new User(1, "Nimmi");
        //Message message = new Message(4, "Hello new msg", user);
        String dataMessageTest = "This is a test data meüääöäüssage..!.1.!";
        Message message = new Message(1, "Test data 123", user, dataMessageTest.getBytes(StandardCharsets.UTF_8));

        AuthenticationPacket authenticationPacket = new AuthenticationPacket(testCredientials);
        send(authenticationPacket);

        //RegisterPacket packet = new RegisterPacket(testCredientials);
        //CreateConversationPacket packet = new CreateConversationPacket(1);
        //MessagePacket packet = new MessagePacket(message);
        //MessageHistoryPacket packet = new MessageHistoryPacket(1, -1);
        //ModifyConversationUserPacket packet = new ModifyConversationUserPacket(ModifyConversationUserPacket.Action.ADD, 10, 1);
        //GetConversationsPacket packet = new GetConversationsPacket(10);
        //SearchUserPacket packet = new SearchUserPacket("Nimmi");
        //GetAttachmentPacket packet = new GetAttachmentPacket("7bff0eae-6dd9-444a-98a6-16a9b4161b66");

        //send(packet);
    }

    @Override
    public void onUnauthorized(WebSocket webSocket) {
        System.out.println("UnauthorizedPacket");
    }

    @Override
    public void onRegister(RegisterPacket packet, WebSocket webSocket) {
        if(packet.getCredentials().isAuthenticated()){
            System.out.println("Registration successful");
            System.out.println("Authenticated");
        } else {
            System.out.println("Registration failed (username already taken)");
        }
    }

    @Override
    public void onAuthenticate(AuthenticationPacket packet, WebSocket webSocket) {
        if(packet.getCredentials().isAuthenticated()){
            System.out.println("Authenticated");
        } else {
            System.out.println("Authentication failed");
        }
    }

    @Override
    public void onMessage(MessagePacket packet, WebSocket webSocket) {
        System.out.println("MessagePacket");
    }

    @Override
    public void onMessageHistory(MessageHistoryPacket packet, WebSocket webSocket) {
        if(packet.getMessages().length == 0) System.out.println("No Messages.");
        else for(Message message : packet.getMessages()) System.out.println(String.format("|%d| %s: %s", message.getId(), message.getUser().getName(), message.getContent()));
    }

    @Override
    public void onGetConversations(GetConversationsPacket packet, WebSocket webSocket) {
        for(Conversation conversation : packet.getConversations()){
            System.out.println(String.format("|%d|%s: %s %d", conversation.getId(), conversation.getMessage().getUser().getName(), conversation.getMessage().getContent(), conversation.getLastActivity()));
        }
    }

    @Override
    public void onCreateConversation(CreateConversationPacket packet, WebSocket webSocket) {
        System.out.println("CreateConversation: " + packet.getConversationId());
    }

    @Override
    public void onModifyConversationUser(ModifyConversationUserPacket packet, WebSocket webSocket) {
        System.out.println("AddUserToConversationPacket");
    }

    @Override
    public void onGetAttachment(GetAttachmentPacket packet, WebSocket webSocket) {
        System.out.println("AttachmentPacket: Size: " + packet.getData().length + ", Data: " + new String(packet.getData()));
    }

    @Override
    public void onSearchUser(SearchUserPacket packet, WebSocket webSocket) {
        if(packet.getResults().length == 0) System.out.println("No Users found.");
        else for(User user : packet.getResults()) System.out.println(user.getName());
    }

    private void send(Packet packet){
        webSocketClient.send(packet.toString());
    }

}