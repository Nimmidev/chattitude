package de.thu.inf.spro.chattitude.backend.database;

import org.junit.Assert;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class FileUploadSQLTest extends SQLTest {

    @Test
    public void addFileTest(){
        byte[] bytes = new byte[10];

        try {
            SecureRandom.getInstanceStrong().nextBytes(bytes);
            fileUploadSQL.add(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getFileTest(){
        byte[] insertBytes = new byte[10];

        try {
            SecureRandom.getInstanceStrong().nextBytes(insertBytes);
            String id = fileUploadSQL.add(insertBytes);
            byte[] bytes = fileUploadSQL.get(id);
            Assert.assertArrayEquals(insertBytes, bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    
}
