package de.thu.inf.spro.chattitude.backend.database;

import de.thu.inf.spro.chattitude.packet.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class UserSQLTest extends SQLTest {
    
    @Test
    public void addUserTest(){
        userSQL.add("addUserTest", "qwer");
    } 
    
    @Test
    public void userExistenceNameTest(){
        String username = "userExistenceNameTest";
        
        boolean exists = userSQL.checkExistence(username);
        Assert.assertFalse(exists);
        
        userSQL.add(username, "qwer");
        exists = userSQL.checkExistence(username);
        Assert.assertTrue(exists);
    }
    
    @Test
    public void userExistenceIdTest(){
        String username = "userExistenceIdTest";
        
        boolean exists = userSQL.checkExistence(-1);
        Assert.assertFalse(exists);

        int userId = userSQL.add(username, "qwer");
        exists = userSQL.checkExistence(userId);
        Assert.assertTrue(exists);
    }
    
    @Test
    public void checkCredentialsTest(){
        String username = "checkCredentialsTest";
        String password = "qwer";

        userSQL.add(username, password);
        boolean verified = userSQL.checkCredentials(username, password);
        Assert.assertTrue(verified);

        verified = userSQL.checkCredentials(username, "----------");
        Assert.assertFalse(verified);
    }
    
    @Test
    public void searchUserTest(){
        final int USERNAME_COUNT = 6;
        
        for(int i = 0; i < USERNAME_COUNT; i++){
            userSQL.add("searchUserTest" + i, "qwer");
        }

        List<User> users = userSQL.search("searchUserTest");
        Assert.assertEquals(users.size(), USERNAME_COUNT);
    }

    @Test
    public void getUserIdByNameTest(){
        String username = "getUserByIdTest";

        int id = userSQL.add(username, "qwer");
        int userId = userSQL.getId(username);

        Assert.assertEquals(id, userId);
    }

    @Test
    public void getUserIdByNameNotFoundTest(){
        int userId = userSQL.getId("getUserByIdNotFoundTest");
        Assert.assertEquals(-1, userId);
    }
    
}
