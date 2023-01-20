package de.thu.inf.spro.chattitude.backend.network;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.Communicator;
import de.thu.inf.spro.chattitude.packet.Credentials;
import de.thu.inf.spro.chattitude.packet.PacketHandler;
import de.thu.inf.spro.chattitude.packet.PacketType;
import de.thu.inf.spro.chattitude.packet.packets.Packet;
import de.thu.inf.spro.chattitude.packet.util.Callback;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer {

    private Communicator communicator;
    private Callback<WebSocket> onDisconnectCallback;

    public WebSocketServer(PacketHandler handler, int port) {
        super(new InetSocketAddress(port));

        communicator = new Communicator(handler);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("New Connection " + webSocket.getRemoteSocketAddress());
        webSocket.send(new Packet(PacketType.CONNECTED).toString());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        Object attachment = webSocket.getAttachment();

        if(onDisconnectCallback != null && attachment != null){
            Credentials credentials = (Credentials) attachment;
            onDisconnectCallback.call(webSocket);
        }

        System.out.println("Connection closed");
    }

    @Override
    public void onMessage(WebSocket webSocket, String s){
        JsonObject packetData = Json.parse(s).asObject();
        Packet packet = Packet.of(packetData);
        communicator.onPacket(packet, webSocket);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.out.println("Error in WebSocket: " + e);
    }

    @Override
    public void onStart() {
        System.out.println("WebSocketServer started");
    }

    public void setOnDisconnectCallback(Callback<WebSocket> callback){
        onDisconnectCallback = callback;
    }

}
