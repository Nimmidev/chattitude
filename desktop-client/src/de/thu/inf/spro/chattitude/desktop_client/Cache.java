package de.thu.inf.spro.chattitude.desktop_client;

import de.thu.inf.spro.chattitude.packet.util.Callback;
import de.thu.inf.spro.chattitude.packet.util.Pair;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class Cache {
    
    private Cache(){}
    
    private static List<String> downloading = new ArrayList<>();
    
    public static byte[] get(String fileId){
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File cachedFile = new File(tmpDir, fileId);
        
        if(cachedFile.exists()){
            if(downloading.contains(fileId)) return null;
            try {
                return Files.readAllBytes(Paths.get(cachedFile.toURI()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    public static void cache(String fileId, DownloadManager downloadManager, Callback<Pair<String, Byte[]>> callback){
        downloading.add(fileId);
        
        downloadManager.download(fileId, objData -> Platform.runLater(() -> {
            byte[] data = downloadManager.objectDataToPrimitive(objData);
            File tmpDir = new File(System.getProperty("java.io.tmpdir"));
            File cachedFile = new File(tmpDir, fileId);
            
            try {
                Files.write(Paths.get(cachedFile.toURI()), data);
                downloading.remove(fileId);
                callback.call(new Pair<>(fileId, downloadManager.primitiveDataToObject(data)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
    
}
