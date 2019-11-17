package de.thu.inf.spro.chattitude.backend.network;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;

class WebSocketServer extends org.java_websocket.server.WebSocketServer {

    private Communicator communicator;

    WebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("New Connection " + webSocket.getRemoteSocketAddress());
        communicator.send(webSocket, Packet.Type.CONNECTED);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        System.out.println("Connection closed");
    }

    @Override
    public void onMessage(WebSocket webSocket, String s){
        if(communicator != null){
            JsonObject packetData = Json.parse(s).asObject();
            int typeId = packetData.get("type").asInt();
            Packet.Type type = Packet.Type.from(typeId);

            communicator.onMessage(type, packetData);
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.out.println("Error in WebSocket: " + e);
    }

    @Override
    public void onStart() {
        System.out.println("WebSocketServer started");
    }

    public void setCommunicator(Communicator communicator){
        this.communicator = communicator;
    }

}
