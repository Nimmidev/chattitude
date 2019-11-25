package de.thu.inf.spro.chattitude.backend;

import java.security.MessageDigest;

import java.sql.SQLException;
import java.util.ArrayList;

class AuthenticationManager {

    static ArrayList Usernames;                                 //werden mit SQLDatenbank ausgetauscht
    static ArrayList Passwords;

    MySqlClient Mysqlclient;

    public AuthenticationManager(MySqlClient mySqlClient) {
        this.Mysqlclient = mySqlClient;

    }

    public boolean register(String Name, String Password) throws SQLException {
        if (Mysqlclient.getUser(Name) == false) {                           //Wenn name noch nicht in Datenbank
            Mysqlclient.addUser(Name, sha256(Password));                    //User hinzufügen
            return true;
        }

        return false;                                                        // Sonst false return
    }


    public boolean login(String Name, String Password) throws SQLException {
        boolean result = Mysqlclient.checkUser(Name, sha256(Password));       // wenn user und passwort richtig return true

        return result;
    }

    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }



}