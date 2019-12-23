package de.thu.inf.spro.chattitude.backend.database;

import org.junit.Assert;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class FileUploadSQLTest extends SQLTest {

    @Test
    public void addFileTest() throws NoSuchAlgorithmException {
        byte[] bytes = new byte[10];

        SecureRandom.getInstanceStrong().nextBytes(bytes);
        fileUploadSQL.add(bytes);
    }

    @Test
    public void getFileTest() throws NoSuchAlgorithmException {
        byte[] insertBytes = new byte[10];

        SecureRandom.getInstanceStrong().nextBytes(insertBytes);
        String id = fileUploadSQL.add(insertBytes);
        byte[] bytes = fileUploadSQL.get(id);
        Assert.assertArrayEquals(insertBytes, bytes);
    }

    @Test
    public void getFileNonExistentTest(){
        byte[] bytes = fileUploadSQL.get("");
        Assert.assertEquals(0, bytes.length);
    }
    
}
