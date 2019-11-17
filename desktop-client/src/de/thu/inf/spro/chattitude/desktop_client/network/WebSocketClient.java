package de.thu.inf.spro.chattitude.desktop_client.network;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.Packet;
import de.thu.inf.spro.chattitude.packet.PacketType;
import org.java_websocket.handshake.ServerHandshake;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

class WebSocketClient extends org.java_websocket.client.WebSocketClient {

    private Communicator communicator;

    WebSocketClient(int port) throws MalformedURLException, URISyntaxException {
        super(new URL("https://localhost:" + port).toURI());
    }

    void setCommunicator(Communicator communicator){
        if(isOpen()) throw new IllegalStateException("Can't set Communicator while WebSocketClient is open.");
        this.communicator = communicator;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake){}

    @Override
    public void onMessage(String s) {
        if(communicator != null){
            JsonObject packetData = Json.parse(s).asObject();
            Packet packet = Packet.of(packetData);
            communicator.onPacket(packet);
        }
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
