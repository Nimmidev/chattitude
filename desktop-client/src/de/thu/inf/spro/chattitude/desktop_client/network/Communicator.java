package de.thu.inf.spro.chattitude.desktop_client.network;

import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.*;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class Communicator {

    private WebSocketClient webSockerClient;

    public Communicator() throws MalformedURLException, URISyntaxException {
        webSockerClient = new WebSocketClient(8080);
        webSockerClient.connect();
        webSockerClient.setCommunicator(this);
    }

    void onPacket(Packet packet){
        PacketType type = packet.getType();
        System.out.println(packet);

        //Dummy implementations for test purposes
        if(type == PacketType.INVALID){
            System.out.println("INVALID packet type");
        } else if(type == PacketType.CONNECTED){
            System.out.println("CONNECTED packet");
            getMessageHistory(0, 0, 4);
        } else if(type == PacketType.REGISTER){
            System.out.println("REGISTER packet");
        } else if(type == PacketType.AUTHENTICATE){
            System.out.println("AUTHENTICATE packet");
        } else if(type == PacketType.MESSAGE){
            MessagePacket mp = (MessagePacket) packet;
            System.out.println("MESSAGE packet: " + mp.getMessage());
        } else if(type == PacketType.MESSAGE_HISTORY){
            System.out.println("MESSAGE_HISTORY packet");
            MessageHistoryPacket mhp = (MessageHistoryPacket) packet;
            System.out.println("Messages: ");
            for(String message : mhp.getMessages()) System.out.println(message);
        } else if(type == PacketType.CONVERSATIONS){
            System.out.println("MESSAGE_HISTORY packet");
            ConversationsPacket cp = (ConversationsPacket) packet;
            System.out.println("Conversations: ");
            for(String conversation : cp.getConversations()) System.out.println(conversation);
        }
    }

    void send(Packet packet){
        webSockerClient.send(packet.toString());
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

    public void close(){
        try {
            webSockerClient.closeBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}