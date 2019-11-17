package de.thu.inf.spro.chattitude.desktop_client;

class User {

    private int id;
    private String name;

    public User(int id, String name){
        this.id = id;
        this.name = name;
    }

    public String getName(){
        return name;
    }

}