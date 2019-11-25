package de.thu.inf.spro.chattitude.packet;

import org.java_websocket.WebSocket;

public abstract class BaseCommunicator {

    private PacketHandler packetHandler;

    public BaseCommunicator(PacketHandler packetHandler){
        this.packetHandler = packetHandler;
    }

    protected void onPacket(Packet packet, WebSocket webSocket){
        PacketType type = packet.getType();

        if(type == PacketType.CONNECTED) packetHandler.onConnected(this, webSocket);
        else if(type == PacketType.REGISTER) packetHandler.onRegister((RegisterPacket) packet, this, webSocket);
        else if(type == PacketType.AUTHENTICATE) packetHandler.onAuthenticate((AuthenticationPacket) packet, this, webSocket);
        else if(type == PacketType.MESSAGE) packetHandler.onMessage((MessagePacket) packet, this, webSocket);
        else if(type == PacketType.MESSAGE_HISTORY) packetHandler.onMessageHistory((MessageHistoryPacket) packet, this, webSocket);
        else if(type == PacketType.CONVERSATIONS) packetHandler.onConversations((ConversationsPacket) packet, this, webSocket);
        else if(type == PacketType.ATTACHMENT) packetHandler.onAttachment((AttachmentPacket) packet, this, webSocket);
        else System.out.println("INVALID packet type");
    }

    public abstract void close();

}
