package de.thu.inf.spro.chattitude.backend;

import de.thu.inf.spro.chattitude.backend.database.MySqlClient;
import de.thu.inf.spro.chattitude.backend.network.WebSocketServer;
import de.thu.inf.spro.chattitude.packet.*;
import de.thu.inf.spro.chattitude.packet.packets.*;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class Server implements PacketHandler {

    public static final int MESSAGE_HISTORY_FETCH_LIMIT = 30;
    public static final int MAX_FILE_UPLOAD_SIZE = 10 * 1000 * 1000;

    private WebSocketServer webSocketServer;
    private MySqlClient mySqlClient;
    private AuthenticationManager authenticationManager;

    private Map<Integer, List<WebSocket>> connections;

    public Server() {
        connections = new HashMap<>();

        mySqlClient = new MySqlClient();
        authenticationManager = new AuthenticationManager(mySqlClient);

        webSocketServer = new WebSocketServer(this,8080);
        webSocketServer.setOnDisconnectCallback(webSocket -> {
            for (Map.Entry<Integer, List<WebSocket>> entry : connections.entrySet()) {
                if (entry.getValue().remove(webSocket)) {
                    if (entry.getValue().isEmpty())
                        connections.remove(entry.getKey());
                    return;
                }
            }
        });
        webSocketServer.start();
    }

    @Override
    public void onConnected(WebSocket webSocket) {
        System.out.println("ConnectedPacket");
    }

    @Override
    public void onUnauthorized(WebSocket webSocket) {
        System.out.println("UnauthorizedPacket");
    }

    @Override
    public void onRegister(RegisterPacket packet, WebSocket webSocket) {
        boolean success = authenticationManager.register(packet.getCredentials(), webSocket);

        if(success) addConnectedUser(packet.getCredentials(), webSocket);
        packet.setSuccessful(success);

        send(webSocket, packet);
    }

    @Override
    public void onAuthenticate(AuthenticationPacket packet, WebSocket webSocket) {
        boolean success = authenticationManager.authenticate(packet.getCredentials(), webSocket);

        if(success) addConnectedUser(packet.getCredentials(), webSocket);
        packet.setSuccessful(success);

        send(webSocket, packet);
    }

    @Override
    public void onMessage(MessagePacket packet, WebSocket webSocket) {
        Message message = packet.getMessage();
        Credentials credentials = webSocket.getAttachment();

        message.setUser(credentials.asUser());
        boolean success = mySqlClient.saveMessage(credentials.getUserId(), message) != -1;
        packet.setSuccessful(success);

        if(success){
            List<User> users = mySqlClient.getConversationUsers(message.getConversationId());
            broadcastPacket(users.toArray(new User[0]), packet);
        }
    }

    @Override
    public void onMessageHistory(MessageHistoryPacket packet, WebSocket webSocket) {
        Credentials credentials = webSocket.getAttachment();
        List<Message> messages = mySqlClient.getMessageHistory(credentials.getUserId(), packet.getConversationId(), packet.getLastMessageId());

        if(messages != null){
            packet.setMessages(messages.toArray(new Message[]{}));
            packet.setSuccessful(true);
        }

        send(webSocket, packet);
    }

    @Override
    public void onGetConversations(GetConversationsPacket packet, WebSocket webSocket) {
        Credentials credentials = webSocket.getAttachment();
        List<Conversation> conversations = mySqlClient.getUserConversations(credentials.getUserId());
        packet.setSuccessful(true);
        packet.setConversations(conversations.toArray(new Conversation[]{}));
        send(webSocket, packet);
    }

    @Override
    public void onCreateConversation(CreateConversationPacket packet, WebSocket webSocket) {
        Credentials credentials = webSocket.getAttachment();
        Conversation conversation = packet.getConversation();
        
        int conversationId = mySqlClient.createConversation(conversation.getName(), credentials.getUserId(), conversation.getUsers());
        boolean success = conversationId != -1;

        conversation.setId(conversationId);
        packet.setSuccessful(success);
        send(webSocket, packet);

        if(success){
            Conversation conversationData = mySqlClient.getConversation(conversationId, -1);
            broadcastPacket(conversationData.getUsers(), user -> new ConversationUpdatedPacket(mySqlClient.getConversation(conversationId, user.getId())));
        }
    }

    @Override
    public void onConversationUpdated(ConversationUpdatedPacket packet, WebSocket webSocket) {
        Credentials credentials = webSocket.getAttachment();
        boolean success = mySqlClient.setConversationName(credentials.getUserId(), packet.getConversation().getId(), packet.getConversation().getName());

        if(success){
            Conversation conversation = mySqlClient.getConversation(packet.getConversation().getId(), -1);
            broadcastPacket(conversation.getUsers(), user -> new ConversationUpdatedPacket(mySqlClient.getConversation(packet.getConversation().getId(), user.getId())));
        }
    }

    @Override
    public void onModifyConversationUser(ModifyConversationUserPacket packet, WebSocket webSocket) {
        Credentials credentials = webSocket.getAttachment();
        boolean success = mySqlClient.modifyConversationUser(packet.getAction(), credentials.getUserId(), packet.getUserId(), packet.getConversationId());

        packet.setSuccessful(success);
        send(webSocket, packet);

        if(success){
            Conversation conversation = mySqlClient.getConversation(packet.getConversationId(), -1);
            broadcastPacket(conversation.getUsers(), user -> new ConversationUpdatedPacket(mySqlClient.getConversation(packet.getConversationId(), user.getId())));
            if (packet.getAction() == ModifyConversationUserPacket.Action.REMOVE) {
                broadcastTo(packet.getUserId(), new ConversationUpdatedPacket(new Conversation(packet.getConversationId(), null, null, new User[0], new int[0])));
            }
        }
    }

    @Override
    public void onGetAttachment(GetAttachmentPacket packet, WebSocket webSocket) {
        byte[] data = mySqlClient.getAttachment(packet.getFieldId());
        packet.setData(data);
        packet.setSuccessful(true);

        send(webSocket, packet);
    }

    @Override
    public void onSearchUser(SearchUserPacket packet, WebSocket webSocket) {
        List<User> results = mySqlClient.searchUsers(packet.getQuery());
        packet.setResult(results.toArray(new User[0]));
        packet.setSuccessful(true);

        send(webSocket, packet);
    }

    void broadcastPacket(User[] users, Packet packet){
        for(User user : users){
            broadcastTo(user.getId(), packet);
        }
    }

    void broadcastPacket(User[] users, Function<User, Packet> fn){
        for(User user : users){
            broadcastTo(user.getId(), fn.apply(user));
        }
    }

    void broadcastTo(int userId, Packet packet) {
        if(connections.containsKey(userId)){
            List<WebSocket> webSockets = connections.get(userId);
            for(WebSocket webSocket : webSockets){
                if(!webSocket.isClosing() && !webSocket.isClosed()) send(webSocket, packet);
            }
        }
    }

    void close() {
        mySqlClient.close();

        try {
            webSocketServer.stop();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addConnectedUser(Credentials credentials, WebSocket webSocket){
        int userId = credentials.getUserId();
        List<WebSocket> webSockets = connections.computeIfAbsent(userId, integer -> new ArrayList<>());
        webSockets.add(webSocket);
    }

    private void send(WebSocket webSocket, Packet packet){
        webSocket.send(packet.toString());
    }

}
