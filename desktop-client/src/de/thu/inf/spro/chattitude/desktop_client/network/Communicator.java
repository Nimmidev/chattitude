package de.thu.inf.spro.chattitude.desktop_client.network;

import com.eclipsesource.json.JsonObject;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class Communicator {

    private WebSocketClient webSockerClient;

    public Communicator() throws MalformedURLException, URISyntaxException {
        webSockerClient = new WebSocketClient(8080);
        webSockerClient.connect();
        webSockerClient.setCommunicator(this);
    }

    void onMessage(Packet.Type type, JsonObject packetData){
        System.out.println(packetData.toString());

        if(type == Packet.Type.INVALID){
            System.out.println("INVALID packet type");
        } else if(type == Packet.Type.CONNECTED){
            System.out.println("CONNECTED packet");
        }
    }

    void send(Packet.Type type){
        send(type, null);
    }

    void send(Packet.Type type, JsonObject data){
        if(data == null) data = new JsonObject();
        data.add("type", type.ordinal());

        webSockerClient.send(data.toString());
    }

    public void close(){
        try {
            webSockerClient.closeBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}