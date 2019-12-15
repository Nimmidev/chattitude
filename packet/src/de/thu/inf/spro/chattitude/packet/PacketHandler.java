package de.thu.inf.spro.chattitude.packet;

import de.thu.inf.spro.chattitude.packet.packets.*;
import org.java_websocket.WebSocket;

// WebSocket will be always null on the client side
public interface PacketHandler {

    void onConnected(WebSocket webSocket);
    void onUnauthorized(WebSocket webSocket);
    void onRegister(RegisterPacket packet, WebSocket webSocket);
    void onAuthenticate(AuthenticationPacket packet, WebSocket webSocket);
    void onMessage(MessagePacket packet, WebSocket webSocket);
    void onMessageHistory(MessageHistoryPacket packet, WebSocket webSocket);
    void onGetConversations(GetConversationsPacket packet, WebSocket webSocket);
    void onCreateConversation(CreateConversationPacket packet, WebSocket webSocket);
    void onConversationUpdated(ConversationUpdatedPacket packet, WebSocket webSocket);
    void onModifyConversationUser(ModifyConversationUserPacket packet, WebSocket webSocket);
    void onGetAttachment(GetAttachmentPacket packet, WebSocket webSocket);
    void onSearchUser(SearchUserPacket packet, WebSocket webSocket);

}
