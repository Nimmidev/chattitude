package de.thu.inf.spro.chattitude.desktop_client;

import de.thu.inf.spro.chattitude.desktop_client.network.WebSocketClient;
import de.thu.inf.spro.chattitude.packet.util.Callback;
import de.thu.inf.spro.chattitude.packet.*;
import de.thu.inf.spro.chattitude.packet.packets.*;
import javafx.scene.control.Alert;
import org.java_websocket.WebSocket;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class Client implements PacketHandler {

    private WebSocketClient webSocketClient;
    private Credentials credentials;

    private Runnable onLoginSuccessful;
    private Runnable onLoginFailed;
    private Callback<Message> onMessage;
    private Callback<Conversation[]> onConversations;
    private Callback<Conversation> onConversationCreated;
    private Callback<MessageHistoryPacket> onMessageHistory;
    private Callback<Conversation> onConversationUpdated;
    private Callback<Credentials> onRegister;
    private Callback<SearchUserPacket> onSearchUser;

    public Client() throws URISyntaxException {
        URI uri;
        String uriStr = System.getenv("SERVER_URL");
        if (uriStr == null) {
            uri = new URI("wss://chattitude.brk.st");
        } else {
            uri = new URI(uriStr);
        }
        webSocketClient = new WebSocketClient(this, uri);
        webSocketClient.connect();
    }

    @Override
    public void onConnected(WebSocket webSocket) {
        
        System.out.println("ConnectedPacket");
        User user = new User(1, "Nimmi");
        //Message message = new Message(4, "Hello new msg", user);
        String dataMessageTest = "This is a test data meüääöäüssage..!.1.!";
        Message message = new Message(1, "Test data: " + System.currentTimeMillis(), user, dataMessageTest.getBytes(StandardCharsets.UTF_8));
        Credentials testCredientials = new Credentials("Nimmi", "qwer");

        //AuthenticationPacket authenticationPacket = new AuthenticationPacket(new Credentials("Nimmi", "qwer"));
        //send(authenticationPacket);

        //RegisterPacket packet = new RegisterPacket(testCredientials);
        //CreateConversationPacket packet = new CreateConversationPacket(1);
        //MessagePacket packet = new MessagePacket(message);
        //MessageHistoryPacket packet = new MessageHistoryPacket(1, 6);
        //ModifyConversationUserPacket packet = new ModifyConversationUserPacket(ModifyConversationUserPacket.Action.ADD, 2, 1);
        //GetConversationsPacket packet = new GetConversationsPacket();
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
        if (packet.getCredentials().isAuthenticated()){
            credentials = packet.getCredentials();
            System.out.println("Registration successful");
            System.out.println("Authenticated");
        } else {
            System.out.println("Registration failed (username already taken)");
        }

        if (onRegister != null) {
            onRegister.call(packet.getCredentials());
        }
    }

    @Override
    public void onAuthenticate(AuthenticationPacket packet, WebSocket webSocket) {
        if(packet.getCredentials().isAuthenticated()){
            System.out.println("Authenticated");
            credentials = packet.getCredentials();
            if (onLoginSuccessful != null) onLoginSuccessful.run();
        } else {
            if (onLoginSuccessful != null) onLoginFailed.run();
        }
    }

    @Override
    public void onMessage(MessagePacket packet, WebSocket webSocket) {
        System.out.println("MessagePacket");
        if (onMessage != null) onMessage.call(packet.getMessage());
    }

    @Override
    public void onMessageHistory(MessageHistoryPacket packet, WebSocket webSocket) {
       if (onMessageHistory != null)
            onMessageHistory.call(packet);
    }

    @Override
    public void onGetConversations(GetConversationsPacket packet, WebSocket webSocket) {
        for(Conversation conversation : packet.getConversations()){
            Message message = conversation.getMessage();
            String messageText = message != null ? message.getContent() : "";
            long timestamp = message != null ? message.getTimestamp() : -1;
            User user = message != null ? message.getUser() : null;
            String username = user != null ? user.getName() : "";
            System.out.println(String.format("|%d|%s: %s %d, Users: %d", conversation.getId(), username, messageText, timestamp, conversation.getUsers().length));
        }

        if (onConversations != null) onConversations.call(packet.getConversations());
    }

    @Override
    public void onCreateConversation(CreateConversationPacket packet, WebSocket webSocket) {
        if (onConversationCreated != null) onConversationCreated.call(packet.getConversation());
    }

    @Override
    public void onConversationUpdated(ConversationUpdatedPacket packet, WebSocket webSocket) {
        if (onConversationUpdated != null) onConversationUpdated.call(packet.getConversation());
    }

    @Override
    public void onModifyConversationUser(ModifyConversationUserPacket packet, WebSocket webSocket) {
        System.out.println(String.format("AddUserToConversationPacket type: %s, user: %d, successful: %b", packet.getAction().name(), packet.getUserId(), packet.wasSuccessful()));
    }

    @Override
    public void onGetAttachment(GetAttachmentPacket packet, WebSocket webSocket) {
        System.out.println("AttachmentPacket: Size: " + packet.getData().length + ", Data: " + new String(packet.getData()));
    }

    @Override
    public void onSearchUser(SearchUserPacket packet, WebSocket webSocket) {
        if(packet.getResults().length == 0) System.out.println("No Users found.");
        else for(User user : packet.getResults()) System.out.println(user.getName());
        if (onSearchUser != null)
            onSearchUser.call(packet);
    }

    public void setOnConversationCreated(Callback<Conversation> onConversationCreated) {
        this.onConversationCreated = onConversationCreated;
    }

    public void setOnConversations(Callback<Conversation[]> onConversations) {
        this.onConversations = onConversations;
    }

    public void setOnLoginSuccessful(Runnable onLoginSuccessful) {
        this.onLoginSuccessful = onLoginSuccessful;
    }

    public void setOnLoginFailed(Runnable onLoginFailed) {
        this.onLoginFailed = onLoginFailed;
    }

    public void setOnMessage(Callback<Message> onMessage) {
        this.onMessage = onMessage;
    }

    public void setOnMessageHistory(Callback<MessageHistoryPacket> onMessageHistory) {
        this.onMessageHistory = onMessageHistory;
    }

    public void setOnConversationUpdated(Callback<Conversation> onConversationUpdated) {
        this.onConversationUpdated = onConversationUpdated;
    }

    public void setOnRegister(Callback<Credentials> onRegister) {
        this.onRegister = onRegister;
    }

    public void setOnSearchUser(Callback<SearchUserPacket> onSearchUser) {
        this.onSearchUser = onSearchUser;
    }

    public void send(Packet packet){
        webSocketClient.send(packet.toString());
    }

    public void close() {
        try {
            webSocketClient.closeBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Credentials getCredentials() {
        return credentials;
    }
}
