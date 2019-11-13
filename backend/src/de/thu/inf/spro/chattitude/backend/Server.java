package de.thu.inf.spro.chattitude.backend;

import org.java_websocket.server.WebSocketServer;

import java.sql.Connection;

public class Server {
    private MySqlClient mySqlClient;
    private WebSocketServer webSocketServer;

    public Server() {
        mySqlClient = new MySqlClient();
        webSocketServer = new ChattitudeWebSocketServer(8080);
        webSocketServer.start();
    }

    public Connection getMySqlConnection() {
        return mySqlClient.getMySqlConnection();
    }

    public void close() {
        mySqlClient.close();
    }
}
