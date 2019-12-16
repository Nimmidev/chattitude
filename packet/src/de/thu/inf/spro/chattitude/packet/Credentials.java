package de.thu.inf.spro.chattitude.packet;

public class Credentials {

    private int userId;
    private String username;
    private String password;
    private boolean authenticated;

    public Credentials(String username, String password){
        this(-1, username, password, false);
    }

    public Credentials(int userId, String username, String password, boolean authenticated){
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.authenticated = authenticated;
    }

    public void setUserId(int userId){
        this.userId = userId;
    }

    public void setAuthenticated(boolean authenticated){
        this.authenticated = authenticated;
    }

    public int getUserId(){
        return userId;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAuthenticated(){
        return authenticated;
    }

    public User asUser(){
        return new User(userId, username);
    }

}
