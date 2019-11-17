package de.thu.inf.spro.chattitude.backend;

import de.thu.inf.spro.chattitude.backend.network.Communicator;

import java.sql.Connection;

public class Server {
    private Communicator communicator;
    private MySqlClient mySqlClient;
    private org.java_websocket.server.WebSocketServer webSocketServer;

    public Server() {
        mySqlClient = new MySqlClient();
        communicator = new Communicator();
    }

    public Connection getMySqlConnection() {
        return mySqlClient.getMySqlConnection();
    }

    public void close() {
        mySqlClient.close();
        communicator.stop();
    }
}
