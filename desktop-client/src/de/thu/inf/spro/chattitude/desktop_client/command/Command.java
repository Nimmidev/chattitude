package de.thu.inf.spro.chattitude.desktop_client.command;

public abstract class Command {
    
    private String command;
    private String description;
    
    public Command(String command, String description){
        this.command = command;
        this.description = description;
    }

    public abstract String exec(String text);
    
    public String execute(String text){
        return exec(text.replace(command + " ", ""));
    }
    
    public boolean match(String cmd){
        return cmd.startsWith(command + " ");
    }
    
    @Override
    public String toString(){
        return String.format("%s: %s\n", command, description);
    }
    
}
