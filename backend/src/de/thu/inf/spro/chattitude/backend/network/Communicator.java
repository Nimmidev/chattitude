package de.thu.inf.spro.chattitude.backend.network;

import de.thu.inf.spro.chattitude.packet.*;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class Communicator extends BaseCommunicator {

    private WebSocketServer webSocketServer;

    public Communicator(PacketHandler packetHandler){
        super(packetHandler);

        webSocketServer = new WebSocketServer(8080);
        webSocketServer.setCommunicator(this);
        webSocketServer.start();
    }

    @Override
    public void onPacket(Packet packet, WebSocket webSocket){
        super.onPacket(packet, webSocket);
    }

    void sendConnectedPackage(WebSocket webSocket){
        webSocket.send(new Packet(PacketType.CONNECTED).toString());
    }

    public void send(WebSocket webSocket, Packet packet){
        webSocket.send(packet.toString());
    }

    @Override
    public void close() {
        try {
            webSocketServer.stop();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}