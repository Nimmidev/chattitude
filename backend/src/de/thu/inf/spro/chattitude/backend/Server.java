package de.thu.inf.spro.chattitude.backend;

import de.thu.inf.spro.chattitude.backend.network.WebSocketServer;
import de.thu.inf.spro.chattitude.packet.*;
import de.thu.inf.spro.chattitude.packet.packets.*;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class Server implements PacketHandler {

    private static final int MESSAGE_HISTORY_FETCH_LIMIT = 10;
    private static final int MAX_FILE_UPLOAD_SIZE = 10 * 1000 * 1000;

    private WebSocketServer webSocketServer;
    private MySqlClient mySqlClient;
    private AuthenticationManager authenticationManager;


    public Server() {
        mySqlClient = new MySqlClient();
        authenticationManager = new AuthenticationManager(mySqlClient);

        webSocketServer = new WebSocketServer(this,8080);
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
        packet.setSuccessful(success);
        send(webSocket, packet);
    }

    @Override
    public void onMessage(MessagePacket packet, WebSocket webSocket) {
        Message message = packet.getMessage();
        Credentials credentials = webSocket.getAttachment();

        if(mySqlClient.checkUserInConversation(credentials.getUserId(), message.getConversationId())){
            if(message.getData().length < MAX_FILE_UPLOAD_SIZE){
                mySqlClient.saveMessage(message);
                packet.setSuccessful(true);
            }
        } else {
            packet.setSuccessful(false);
        }

        //TODO: Send messages to connected recipients
    }

    @Override
    public void onMessageHistory(MessageHistoryPacket packet, WebSocket webSocket) {
        Credentials credentials = webSocket.getAttachment();

        if(mySqlClient.checkUserInConversation(credentials.getUserId(), packet.getConversationId())){
            List<Message> messages =  mySqlClient.getMessageHistory(packet.getConversationId(), packet.getOffset(), MESSAGE_HISTORY_FETCH_LIMIT);
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
        List<Conversation> conversations = mySqlClient.getUserConversations(packet.getUserId());
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

    //TODO: Admin check
    @Override
    public void onModifyConversationUser(ModifyConversationUserPacket packet, WebSocket webSocket) {
        if(packet.getAction() == ModifyConversationUserPacket.Action.REMOVE){
            mySqlClient.removeUserFromConversation(packet.getConversationId(), packet.getUserId());
        } else {
            mySqlClient.addUserToConversation(packet.getConversationId(), packet.getUserId());
        }
        packet.setSuccessful(true);
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
