package de.thu.inf.spro.chattitude.backend;

import de.thu.inf.spro.chattitude.backend.network.Communicator;
import de.thu.inf.spro.chattitude.packet.*;
import org.java_websocket.WebSocket;

import java.sql.Connection;

public class Server implements PacketHandler {
    private Communicator communicator;
    private MySqlClient mySqlClient;
    private AuthenticationManager authennticationManager;


    public Server() {
        mySqlClient = new MySqlClient();
        communicator = new Communicator(this);
    }

    @Override
    public void onConnected(BaseCommunicator baseCommunicator, WebSocket webSocket) {
        System.out.println("ConnectedPacket");
    }

    @Override
    public void onRegister(RegisterPacket packet, BaseCommunicator baseCommunicator, WebSocket webSocket) {
        Communicator communicator = (Communicator) baseCommunicator;
        System.out.println("RegisterPacket");
    }

    @Override
    public void onAuthenticate(AuthenticationPacket packet, BaseCommunicator baseCommunicator, WebSocket webSocket) {
        Communicator communicator = (Communicator) baseCommunicator;
        System.out.println("AuthenticationPacket");
    }

    @Override
    public void onMessage(MessagePacket packet, BaseCommunicator baseCommunicator, WebSocket webSocket) {
        Communicator communicator = (Communicator) baseCommunicator;
        System.out.println("MessagePacket");
    }

    @Override
    public void onMessageHistory(MessageHistoryPacket packet, BaseCommunicator baseCommunicator, WebSocket webSocket) {
        Communicator communicator = (Communicator) baseCommunicator;
        System.out.println("MessageHistoryPacket");
    }

    @Override
    public void onConversations(ConversationsPacket packet, BaseCommunicator baseCommunicator, WebSocket webSocket) {
        Communicator communicator = (Communicator) baseCommunicator;
        System.out.println("ConversationsPacket");
    }

    @Override
    public void onAttachment(AttachmentPacket packet, BaseCommunicator baseCommunicator, WebSocket webSocket) {
        Communicator communicator = (Communicator) baseCommunicator;
        System.out.println("AttachmentPacket");
    }

    public Connection getMySqlConnection() {
        return mySqlClient.getMySqlConnection();
    }

    public void close() {
        mySqlClient.close();
        communicator.close();
    }
}
