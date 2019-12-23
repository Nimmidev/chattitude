package de.thu.inf.spro.chattitude.backend;

import de.thu.inf.spro.chattitude.backend.database.MySqlClient;
import de.thu.inf.spro.chattitude.packet.Credentials;
import org.java_websocket.WebSocket;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

class AuthenticationManager {

    private MySqlClient mysqlClient;

    AuthenticationManager(MySqlClient mySqlClient) {
        this.mysqlClient = mySqlClient;
    }

    boolean register(Credentials credentials, WebSocket webSocket){
        if(!mysqlClient.checkUserExistence(credentials.getUsername())){
            mysqlClient.addUser(credentials.getUsername(), sha256(credentials.getPassword()));
            updateCredentials(credentials, webSocket, true);
            return true;
        }

        return false;
    }


    boolean authenticate(Credentials credentials, WebSocket webSocket){
        boolean authenticated = mysqlClient.checkUserCredentials(credentials.getUsername(), sha256(credentials.getPassword()));
        updateCredentials(credentials, webSocket, authenticated);

        return authenticated;
    }

    private void updateCredentials(Credentials credentials, WebSocket webSocket, boolean success){
        int userId = mysqlClient.getUserId(credentials.getUsername());

        credentials.setUserId(userId);
        credentials.setAuthenticated(success);
        webSocket.setAttachment(credentials);
    }

    private String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }



}