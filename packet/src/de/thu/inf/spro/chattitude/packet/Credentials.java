package de.thu.inf.spro.chattitude.packet;

public class Credentials {

    private String username;
    private String password;
    private boolean authenticated;

    public Credentials(String username, String password){
        this(username, password, false);
    }

    public Credentials(String username, String password, boolean authenticated){
        this.username = username;
        this.password = password;
        this.authenticated = authenticated;
    }

    public void setAuthenticated(boolean authenticated){
        this.authenticated = authenticated;
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

}
