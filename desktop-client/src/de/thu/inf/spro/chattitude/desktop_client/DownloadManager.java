package de.thu.inf.spro.chattitude.desktop_client;

import de.thu.inf.spro.chattitude.packet.packets.GetAttachmentPacket;
import de.thu.inf.spro.chattitude.packet.util.Callback;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DownloadManager implements Callback<GetAttachmentPacket> {
    
    private Client client;
    private Map<String, Callback<Byte[]>> callbackMap;
    
    public DownloadManager(Client client){
        this.client = client;
        callbackMap = new HashMap<>();
    }
    
    public void download(String fileId, Callback<Byte[]> callback){
        String identifier = fileId + System.currentTimeMillis();
        callbackMap.put(identifier, callback);
        GetAttachmentPacket packet = new GetAttachmentPacket(fileId, identifier);
        client.send(packet);
    }
    
    @Override
    public void call(GetAttachmentPacket packet) {
        if(callbackMap.containsKey(packet.getRequestIdentifier())){
            Byte[] bytes = primitiveDataToObject(packet.getData());
            callbackMap.get(packet.getRequestIdentifier()).call(bytes);
            callbackMap.remove(packet.getRequestIdentifier());
        }
    }

    public Byte[] primitiveDataToObject(byte[] data){
        Byte[] bytes = new Byte[data.length];
        for(int i = 0; i < data.length; i++) bytes[i] = data[i];
        return bytes;
    }

    public byte[] objectDataToPrimitive(Byte[] data){
        byte[] bytes = new byte[data.length];
        for(int i = 0; i < data.length; i++) bytes[i] = data[i];
        return bytes;
    }
    
    public String chooseDirectory(Window window){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(window);
        return file.getAbsolutePath();
    }
    
    public void saveTo(String path, String filename, byte[] data){
        File file = new File(path, filename);
        try {
            Files.write(Paths.get(file.toURI()), data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
