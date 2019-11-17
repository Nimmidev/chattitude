package de.thu.inf.spro.chattitude.desktop_client;

class SingleChat extends Chat {

    private User recipiant;

    public SingleChat(int id, User recipiant) {
        super(id);

        this.recipiant = recipiant;
    }

    @Override
    public String getTitle() {
        return recipiant.getName();
    }
}