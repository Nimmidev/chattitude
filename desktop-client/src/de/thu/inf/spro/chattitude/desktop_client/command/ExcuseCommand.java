package de.thu.inf.spro.chattitude.desktop_client.command;

import de.thu.inf.spro.chattitude.desktop_client.message.TextMessage;
import de.thu.inf.spro.chattitude.desktop_client.ui.controller.MainScreenController;
import de.thu.inf.spro.chattitude.packet.packets.MessagePacket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ExcuseCommand extends Command {
    
    private static final URL url;
    
    static {
        URL _url = null;
        try {
            _url = new URL("https://ausrede.brk.st/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        url = _url;
    }
    
    public ExcuseCommand() {
        super("/excuse, /ex", "Provide a random excuse.");
    }

    @Override
    public boolean match(String cmd){
        return cmd.startsWith("/excuse") || cmd.startsWith("/ex");
    }
    
    @Override
    public String exec(MainScreenController controller, String text) {
        try {
            String excuse = getExcuse();
            TextMessage message = new TextMessage(controller.getSelectedConversation().getId(), excuse);
            controller.getClient().send(new MessagePacket(message.asMessage()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private String getExcuse() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "curl/0");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
            return reader.readLine();
        }
    }
    
}
