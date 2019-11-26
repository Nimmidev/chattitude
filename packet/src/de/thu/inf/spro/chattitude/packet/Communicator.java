package de.thu.inf.spro.chattitude.packet;

import de.thu.inf.spro.chattitude.packet.packets.*;
import org.java_websocket.WebSocket;

public class Communicator {

    private PacketHandler packetHandler;

    public Communicator(PacketHandler packetHandler){
        this.packetHandler = packetHandler;
    }

    public void onPacket(Packet packet, WebSocket webSocket){
        PacketType type = packet.getType();

        if(type == PacketType.CONNECTED) packetHandler.onConnected(webSocket);
        else if(type == PacketType.UNAUTHORIZED) packetHandler.onUnauthorized(webSocket);
        else if(type == PacketType.REGISTER) packetHandler.onRegister((RegisterPacket) packet, webSocket);
        else if(type == PacketType.AUTHENTICATE) packetHandler.onAuthenticate((AuthenticationPacket) packet, webSocket);
        else if(webSocket == null || userIsAuthenticated(webSocket.getAttachment())){
            if(type == PacketType.MESSAGE) packetHandler.onMessage((MessagePacket) packet, webSocket);
            else if(type == PacketType.MESSAGE_HISTORY) packetHandler.onMessageHistory((MessageHistoryPacket) packet, webSocket);
            else if(type == PacketType.GET_CONVERSATIONS) packetHandler.onGetConversations((GetConversationsPacket) packet, webSocket);
            else if(type == PacketType.CREATE_CONVERSATION) packetHandler.onCreateConversation((CreateConversationPacket) packet, webSocket);
            else if(type == PacketType.MODIFY_CONVERSATION_USER) packetHandler.onModifyConversationUser((ModifyConversationUserPacket) packet, webSocket);
            else if(type == PacketType.GET_ATTACHMENT) packetHandler.onGetAttachment((GetAttachmentPacket) packet, webSocket);
            else if(type == PacketType.SEARCH_USER) packetHandler.onSearchUser((SearchUserPacket) packet, webSocket);
            else System.out.println("INVALID packet type");
        } else {
            System.out.println("Error: Unauthorized Access");
            webSocket.send(new Packet(PacketType.UNAUTHORIZED).toString());
        }
    }

    private boolean userIsAuthenticated(Object attachment){
        if(attachment == null) return false;
        Credentials credentials = (Credentials) attachment;
        return credentials.isAuthenticated();
    }

}
