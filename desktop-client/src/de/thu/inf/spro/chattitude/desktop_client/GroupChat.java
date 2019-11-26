package de.thu.inf.spro.chattitude.desktop_client;

import de.thu.inf.spro.chattitude.packet.User;

import java.util.List;

class GroupChat extends Chat {

    private String title;
    private List<User> users;

    public GroupChat(int id, String title) {
        super(id);

        this.title = title;
    }

    public void addUser(User user){
        users.add(user);
    }

    public void deleteUser(User user){
        users.remove(user);
    }

    @Override
    public String getTitle() {
        return title;
    }
}