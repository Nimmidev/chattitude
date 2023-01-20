package de.thu.inf.spro.chattitude.desktop_client.command;

import de.thu.inf.spro.chattitude.desktop_client.ui.controller.MainScreenController;

import java.util.List;

public class HelpCommand extends Command {
    
    private List<Command> commands;
    
    public HelpCommand(List<Command> commands) {
        super("/help, /?", "Print all available commands.");
        this.commands = commands;
    }

    @Override
    public boolean match(String cmd){
        return cmd.startsWith("/help") || cmd.startsWith("/?");
    }
    
    @Override
    public String exec(MainScreenController controller, String text) {
        StringBuilder builder = new StringBuilder("Available commands: \n");

        for(Command command : commands) builder.append(command);

        return builder.toString();
    }
    
}
