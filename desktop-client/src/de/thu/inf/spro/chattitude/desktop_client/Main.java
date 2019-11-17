package de.thu.inf.spro.chattitude.desktop_client;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class Main {

    public static void main(String[] args) {
        try {
            new Client();
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
