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
    public void userExistenceTest(){
        String username = "userExistenceTest";

        userSQL.add(username, "qwer");
        boolean exists = userSQL.checkExistence(username);
        Assert.assertTrue(exists);

        exists = userSQL.checkExistence("----------");
        Assert.assertFalse(exists);
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
    public void getUserByIdTest(){
        String username = "getUserByIdTest";
        
        int id = userSQL.add(username, "qwer");
        int userId = userSQL.getId(username);
        
        Assert.assertEquals(id, userId);
    }
    
}
