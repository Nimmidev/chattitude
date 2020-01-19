package de.thu.inf.spro.chattitude.desktop_client.command;

import de.thu.inf.spro.chattitude.desktop_client.ui.controller.MainScreenController;

import java.util.ArrayList;
import java.util.List;

public final class CommandParser {
    
    private static final List<Command> commands;
    
    static {
        commands = new ArrayList<>();
        commands.add(new ReplySubstitutionCommand("/stack", "https://stackoverflow.com/search?q=", "Sends a link to the StackOverflow search results."));
        commands.add(new SubstitutionCommand("/yt", "https://www.youtube.com/results?search_query=", "Sends a link to the Youtube search results."));
        commands.add(new ReplySubstitutionCommand("/lmgtfy", "https://lmgtfy.com/?q=", "Sends a link to Let me Google that for you."));
    }
    
    private MainScreenController controller;
    
    public CommandParser(MainScreenController controller){
        this.controller = controller;
    }
    
    public String parse(String text){
        if(text.equals("/help") || text.equals("/?")) return printHelp();
        for(Command command : commands){
            if(command.match(text)){
                return command.execute(controller, text);
            }
        }
        
        return text;
    }
    
    private String printHelp(){
        StringBuilder builder = new StringBuilder("Available commands: \n");
        
        for(Command command :commands) builder.append(command);
        
        return builder.toString();
    }
    
}
