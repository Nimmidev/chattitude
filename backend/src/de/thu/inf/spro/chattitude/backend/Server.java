package de.thu.inf.spro.chattitude.backend;

import de.thu.inf.spro.chattitude.backend.network.WebSocketServer;
import de.thu.inf.spro.chattitude.packet.*;
import de.thu.inf.spro.chattitude.packet.packets.*;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server implements PacketHandler {

    private static final int MESSAGE_HISTORY_FETCH_LIMIT = 10;
    private static final int MAX_FILE_UPLOAD_SIZE = 10 * 1000 * 1000;

    private WebSocketServer webSocketServer;
    private MySqlClient mySqlClient;
    private AuthenticationManager authenticationManager;

    private Map<Integer, WebSocket> connections;

    public Server() {
        connections = new HashMap<>();

        mySqlClient = new MySqlClient();
        authenticationManager = new AuthenticationManager(mySqlClient);

        webSocketServer = new WebSocketServer(this,8080);
        webSocketServer.setOnDisconnectCallback(integer -> {
            connections.remove(integer);
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
        packet.setSuccessful(success);
        send(webSocket, packet);
    }

    @Override
    public void onAuthenticate(AuthenticationPacket packet, WebSocket webSocket) {
        boolean success = authenticationManager.authenticate(packet.getCredentials(), webSocket);

        if(success) connections.put(packet.getCredentials().getUserId(), webSocket);

        packet.setSuccessful(success);
        send(webSocket, packet);
    }

    @Override
    public void onMessage(MessagePacket packet, WebSocket webSocket) {
        Message message = packet.getMessage();
        Credentials credentials = webSocket.getAttachment();
        message.setUser(new User(credentials.getUserId(), credentials.getUsername()));

        if(mySqlClient.checkUserInConversation(credentials.getUserId(), message.getConversationId())){
            if(message.getData().length < MAX_FILE_UPLOAD_SIZE){
                mySqlClient.saveMessage(message);
                packet.setSuccessful(true);
            }
        } else {
            packet.setSuccessful(false);
        }

        List<User> users = mySqlClient.getConversationUsers(packet.getMessage().getConversationId());

        for(User user : users){
            if(connections.containsKey(user.getId())){
                WebSocket userWebSocket = connections.get(user.getId());
                if(!userWebSocket.isClosing() && !userWebSocket.isClosed()) send(userWebSocket, packet);
            }
        }
    }

    @Override
    public void onMessageHistory(MessageHistoryPacket packet, WebSocket webSocket) {
        Credentials credentials = webSocket.getAttachment();

        if(mySqlClient.checkUserInConversation(credentials.getUserId(), packet.getConversationId())){
            List<Message> messages =  mySqlClient.getMessageHistory(packet.getConversationId(), packet.getLastMessageId(), MESSAGE_HISTORY_FETCH_LIMIT);
            packet.setMessages(messages.toArray(new Message[]{}));
            packet.setSuccessful(true);
            System.out.println("success");
        } else {
            packet.setSuccessful(false);
            System.out.println("failure");
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
        boolean success = false;
        int conversationId = mySqlClient.createConversation();

        if(conversationId != -1){
            success = true;
            packet.setConversationId(conversationId);
            for(int userId : packet.getUserIds()) mySqlClient.addUserToConversation(conversationId, userId);
        }

        packet.setSuccessful(success);
        send(webSocket, packet);
    }

    @Override
    public void onConversationUpdated(ConversationUpdatedPacket packet, WebSocket webSocket) {
        System.out.println("ConversationUpdatedPacket: " + packet.getConversation().getId());
    }

    @Override
    public void onModifyConversationUser(ModifyConversationUserPacket packet, WebSocket webSocket) {
        Credentials credentials = webSocket.getAttachment();
        boolean isAdmin = mySqlClient.checkUserIsAdmin(credentials.getUserId(), packet.getConversationId());

        if(isAdmin){
            if(packet.getAction() == ModifyConversationUserPacket.Action.REMOVE){
                mySqlClient.removeUserFromConversation(packet.getConversationId(), packet.getUserId());
            } else if(packet.getAction() == ModifyConversationUserPacket.Action.ADD){
                mySqlClient.addUserToConversation(packet.getConversationId(), packet.getUserId());
            } else if(packet.getAction() == ModifyConversationUserPacket.Action.PROMOTE_ADMIN){
                mySqlClient.updateConversationAdmin(packet.getConversationId(), packet.getUserId(), true);
            } else if(packet.getAction() == ModifyConversationUserPacket.Action.DEMOTE_ADMIN){
                mySqlClient.updateConversationAdmin(packet.getConversationId(), packet.getUserId(), false);
            }
        }

        packet.setSuccessful(isAdmin);
        send(webSocket, packet);
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
        packet.setResult(results.toArray(new User[results.size()]));
        packet.setSuccessful(true);

        send(webSocket, packet);
    }

    public Connection getMySqlConnection() {
        return mySqlClient.getMySqlConnection();
    }

    public void close() {
        mySqlClient.close();

        try {
            webSocketServer.stop();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void send(WebSocket webSocket, Packet packet){
        webSocket.send(packet.toString());
    }

}
