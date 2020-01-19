package de.thu.inf.spro.chattitude.desktop_client.command;

import de.thu.inf.spro.chattitude.desktop_client.ui.controller.MainScreenController;

public abstract class Command {
    
    private String command;
    private String description;
    
    public Command(String command, String description){
        this.command = command;
        this.description = description;
    }

    public abstract String exec(MainScreenController controller, String text);
    
    public String execute(MainScreenController controller, String text){
        return exec(controller, text.replace(command, "").trim());
    }
    
    public boolean match(String cmd){
        return cmd.startsWith(command);
    }
    
    @Override
    public String toString(){
        return String.format("%s: %s\n", command, description);
    }
    
}
