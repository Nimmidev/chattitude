package de.thu.inf.spro.chattitude.desktop_client.command;

import de.thu.inf.spro.chattitude.desktop_client.ui.controller.MainScreenController;

public class SubstitutionCommand extends Command {
    
    private String url;
    
    public SubstitutionCommand(String cmd, String url, String description) {
        super(cmd, description);
        this.url = url;
    }

    @Override
    public String exec(MainScreenController controller, String text) {
        return url + text.replace(" ", "+");
    }
}
