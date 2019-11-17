package de.thu.inf.spro.chattitude.backend.network;

import com.eclipsesource.json.JsonObject;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class Communicator {

    private WebSocketServer webSocketServer;

    public Communicator(){
        webSocketServer = new WebSocketServer(8080);
        webSocketServer.setCommunicator(this);
        webSocketServer.start();
    }

    void onMessage(Packet.Type type, JsonObject packetData){
        System.out.println(packetData.toString());

        if(type == Packet.Type.INVALID){
            System.out.println("INVALID packet type");
        } else if(type == Packet.Type.CONNECTED){
            System.out.println("CONNECTED packet");
        }
    }

    void send(WebSocket webSocket, Packet.Type type){
        send(webSocket, type, null);
    }

    void send(WebSocket webSocket, Packet.Type type, JsonObject data){
        if(data == null) data = new JsonObject();
        data.add("type", type.ordinal());

        webSocket.send(data.toString());
    }

    public void stop() {
        try {
            webSocketServer.stop();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}