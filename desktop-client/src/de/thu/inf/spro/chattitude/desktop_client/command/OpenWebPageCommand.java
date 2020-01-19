package de.thu.inf.spro.chattitude.desktop_client.command;

import de.thu.inf.spro.chattitude.desktop_client.ui.App;
import de.thu.inf.spro.chattitude.desktop_client.ui.controller.MainScreenController;

public class OpenWebPageCommand extends Command {
    
    private String url;
    
    public OpenWebPageCommand(String command, String url, String description) {
        super(command, description);
        this.url = url;
    }

    @Override
    public String exec(MainScreenController controller, String text) {
        String link =  url + text.replace(" ", "+");
        App.getInstance().getHostServices().showDocument(link);
        return null;
    }
}
