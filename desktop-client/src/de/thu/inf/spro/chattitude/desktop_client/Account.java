package de.thu.inf.spro.chattitude.desktop_client;

import java.util.ArrayList;
import java.util.List;

class Account {

    private List<Chat> chats;

    public Account(){
        chats = new ArrayList<>();
    }

    public List<Chat> getChats(){
        return chats;
    }

}