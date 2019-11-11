package de.thu.inf.spro.chattitude.backend;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class ChattitudeWebSocketServer extends WebSocketServer {

    public ChattitudeWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("Neue Verbindung " + webSocket.getRemoteSocketAddress());
        webSocket.send("Guten Tag! Sind Sie mit dem ChattitudeWebSocketServer verbunden");
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        System.out.println("Verbindunge getrennt");
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        System.out.println("Habe erhalten: " + s);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }
}
