package de.thu.inf.spro.chattitude.desktop_client;

import java.io.*;

public enum FileType {

    UNKNOWN,
    IMAGE,
    AUDIO;
    
    private static FileType[] _values = values();

    public static FileType from(int i){
        if(i < _values.length) return _values[i];
        return FileType.UNKNOWN;
    }
    
    public static FileType get(File file){
        try (InputStream in = new FileInputStream(file)) {
            byte[] buffer = new byte[10];
            if(in.read(buffer) == 10){
                return get(buffer);    
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return UNKNOWN;
    }

    public static FileType get(byte[] buffer){
        if(buffer != null){
            if(checkImageHeaders(buffer)) return IMAGE;
            if(checkAudioHeaders(buffer)) return AUDIO;
        }

        return UNKNOWN;
    }
    
    private static boolean checkImageHeaders(byte[] buffer){
        return checkPNG(buffer) || checkJPEG(buffer);
    }

    private static boolean checkPNG(byte[] buffer){
        return buffer[1] == 'P' && buffer[2] == 'N' && buffer[3] == 'G';
    }
    
    private static boolean checkJPEG(byte[] buffer){
        return buffer[6] == 'J' && buffer[7] == 'F' && buffer[8] == 'I' && buffer[9] == 'F';
    }
    
    private static boolean checkAudioHeaders(byte[] buffer){
        return checkMP3(buffer);
    }
    
    private static boolean checkMP3(byte[] buffer){
        return (buffer[0] == 0xFF && buffer[1] == 0xFB) ||
                (buffer[0] == 0x49 && buffer[1] == 0x44 && buffer[2] == 0x33);
    }
    
}
