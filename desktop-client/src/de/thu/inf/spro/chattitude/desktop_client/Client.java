package de.thu.inf.spro.chattitude.desktop_client;

import de.thu.inf.spro.chattitude.desktop_client.network.Communicator;
import de.thu.inf.spro.chattitude.desktop_client.ui.Window;
import de.thu.inf.spro.chattitude.packet.*;
import javafx.application.Application;
import org.java_websocket.WebSocket;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

class Client implements PacketHandler {

    private Window window;
    private Communicator communicator;

    public Client() throws MalformedURLException, URISyntaxException {
        window = new Window();
        communicator = new Communicator(this);

        window.setOnCloseListener(() -> communicator.close());

        Application.launch(Window.class);
    }

    @Override
    public void onConnected(BaseCommunicator baseCommunicator, WebSocket webSocket) {
        Communicator communicator = (Communicator) baseCommunicator;
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

}