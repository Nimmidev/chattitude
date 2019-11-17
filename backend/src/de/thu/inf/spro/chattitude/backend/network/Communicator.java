package de.thu.inf.spro.chattitude.backend.network;

import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.*;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class Communicator {

    private WebSocketServer webSocketServer;

    public Communicator(){
        webSocketServer = new WebSocketServer(8080);
        webSocketServer.setCommunicator(this);
        webSocketServer.start();
    }

    void onPacket(WebSocket webSocket, Packet packet){
        PacketType type = packet.getType();
        System.out.println(packet);

        //Dummy implementations for test purposes
        if(type == PacketType.INVALID){
            System.out.println("INVALID packet type");
        } else if(type == PacketType.CONNECTED){
            System.out.println("CONNECTED packet");
        } else if(type == PacketType.REGISTER){
            RegisterPacket rp = (RegisterPacket) packet;
            System.out.println(String.format("Register | Username: %s, Password: %s", rp.getUsername(), rp.getPassword()));
            RegisterPacket rpr = new RegisterPacket(rp.getUsername(), rp.getPassword(), true);
            send(webSocket, rpr);
        } else if(type == PacketType.AUTHENTICATE){
            AuthenticationPacket ap = (AuthenticationPacket) packet;
            System.out.println(String.format("Authenticate | Username: %s, Password: %s", ap.getUsername(), ap.getPassword()));
            AuthenticationPacket apr = new AuthenticationPacket(ap.getUsername(), ap.getPassword(), true);
            send(webSocket, apr);
        } else if(type == PacketType.MESSAGE){
            MessagePacket mp = (MessagePacket) packet;
            System.out.println("Message | " + mp.getMessage());
            send(webSocket, mp);
        } else if(type == PacketType.MESSAGE_HISTORY){
            MessageHistoryPacket mhp = (MessageHistoryPacket) packet;
            System.out.println(String.format("MESSAGE_HISTORY | conversationId: %d, offset: %d, amount: %d", mhp.getConversationId(), mhp.getOffset(), mhp.getAmount()));
            MessageHistoryPacket mhpr = new MessageHistoryPacket(mhp.getConversationId(), mhp.getOffset(), 4, new String[]{"Hello", "how", "are", "you"});
            send(webSocket, mhpr);
        } else if(type == PacketType.CONVERSATIONS){
            ConversationsPacket cp = (ConversationsPacket) packet;
            System.out.println(String.format("CONVERSATIONS | userId: %d", cp.getUserId()));
            ConversationsPacket cpr = new ConversationsPacket(cp.getUserId(), new String[]{"Jan", "Paso", "Moritz", "Nimmi"});
            send(webSocket, cpr);
        }
    }

    void send(WebSocket webSocket, Packet packet){
        webSocket.send(packet.toString());
    }

    public void stop() {
        try {
            webSocketServer.stop();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}