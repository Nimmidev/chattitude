package de.thu.inf.spro.chattitude.desktop_client;

import de.thu.inf.spro.chattitude.desktop_client.network.Communicator;
import de.thu.inf.spro.chattitude.desktop_client.ui.Window;
import javafx.application.Application;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

class Client {

    private Window window;
    private Communicator communicator;

    public Client() throws MalformedURLException, URISyntaxException {
        window = new Window();
        communicator = new Communicator();

        window.setOnCloseListener(() -> communicator.close());

        Application.launch(Window.class);
    }

}