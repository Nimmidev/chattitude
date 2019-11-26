package de.thu.inf.spro.chattitude.desktop_client.network;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.Communicator;
import de.thu.inf.spro.chattitude.packet.PacketHandler;
import de.thu.inf.spro.chattitude.packet.packets.Packet;
import org.java_websocket.handshake.ServerHandshake;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class WebSocketClient extends org.java_websocket.client.WebSocketClient {

    private Communicator communicator;

    public WebSocketClient(PacketHandler handler, int port) throws MalformedURLException, URISyntaxException {
        super(new URL("https://localhost:" + port).toURI());

        communicator = new Communicator(handler);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake){}

    @Override
    public void onMessage(String s) {
        JsonObject packetData = Json.parse(s).asObject();
        Packet packet = Packet.of(packetData);
        communicator.onPacket(packet, null);
    }

    @Override
    public void onClose(int code, String reason, boolean b) {
        System.out.println("Closed with exit code " + code + ". Additional info: " + reason);
    }

    @Override
    public void onError(Exception e) {
        if(e.getMessage().equals("Connection refused: connect")){
            System.out.println("Server currently offline");
        } else {
            e.printStackTrace();
        }
    }

}
