package de.thu.inf.spro.chattitude.packet;

import org.java_websocket.WebSocket;

// WebSocket will be always null on the client side
public interface PacketHandler {

    void onConnected(BaseCommunicator baseCommunicator, WebSocket webSocket);
    void onRegister(RegisterPacket packet, BaseCommunicator baseCommunicator, WebSocket webSocket);
    void onAuthenticate(AuthenticationPacket packet, BaseCommunicator baseCommunicator, WebSocket webSocket);
    void onMessage(MessagePacket packet, BaseCommunicator baseCommunicator, WebSocket webSocket);
    void onMessageHistory(MessageHistoryPacket packet, BaseCommunicator baseCommunicator, WebSocket webSocket);
    void onConversations(ConversationsPacket packet, BaseCommunicator baseCommunicator, WebSocket webSocket);
    void onAttachment(AttachmentPacket packet, BaseCommunicator baseCommunicator, WebSocket webSocket);

}
