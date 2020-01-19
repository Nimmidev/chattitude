package de.thu.inf.spro.chattitude.desktop_client.command;

public class SubstitutionCommand extends Command {
    
    private String url;
    
    public SubstitutionCommand(String cmd, String url, String description) {
        super(cmd, description);
        this.url = url;
    }

    @Override
    public String exec(String text) {
        return url + text.replace(" ", "+");
    }
}
