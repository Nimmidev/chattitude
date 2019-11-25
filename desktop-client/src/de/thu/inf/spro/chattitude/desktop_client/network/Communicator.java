package de.thu.inf.spro.chattitude.desktop_client.network;

import de.thu.inf.spro.chattitude.packet.*;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class Communicator extends BaseCommunicator {

    private WebSocketClient webSocketClient;

    public Communicator(PacketHandler packetHandler) throws MalformedURLException, URISyntaxException {
        super(packetHandler);

        webSocketClient = new WebSocketClient(8080);
        webSocketClient.connect();
        webSocketClient.setCommunicator(this);
    }

    void onPacket(Packet packet){
        super.onPacket(packet, null);
    }

    private void send(Packet packet){
        webSocketClient.send(packet.toString());
    }

    public void register(String username, String password){
        RegisterPacket registerPacket = new RegisterPacket(username, password);
        send(registerPacket);
    }

    public void authenticate(String username, String password){
        AuthenticationPacket authenticationPacket = new AuthenticationPacket(username, password);
        send(authenticationPacket);
    }

    public void sendMessage(int userId, int conversationId, String message){
        MessagePacket messagePacket = new MessagePacket(userId, conversationId, message);
        send(messagePacket);
    }

    public void getMessageHistory(int conversationId, int offset, int amount){
        MessageHistoryPacket messageHistoryPacket = new MessageHistoryPacket(conversationId, offset, amount);
        send(messageHistoryPacket);
    }

    public void getConversations(int userId){
        ConversationsPacket conversationsPacket = new ConversationsPacket(userId);
        send(conversationsPacket);
    }

    public void getAttachment(String uuid){
        AttachmentPacket attachmentPacket = new AttachmentPacket(uuid);
        send(attachmentPacket);
    }

    @Override
    public void close(){
        try {
            webSocketClient.closeBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}