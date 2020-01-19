package de.thu.inf.spro.chattitude.desktop_client;

import de.thu.inf.spro.chattitude.desktop_client.ui.App;
import de.thu.inf.spro.chattitude.desktop_client.ui.controller.MainScreenController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public final class Notification {
    
    private Notification(){}
    
    public static void send(String caption, String text){
        if(MainScreenController.IS_MINIMIZED){
            SystemTray systemTray = SystemTray.getSystemTray();

            URL url = Notification.class.getResource("/LogoPicRound.png");
            System.out.println(url.toString());
            try {
                Image image = ImageIO.read(url);
                TrayIcon trayIcon = new TrayIcon(image);
                systemTray.add(trayIcon);
                trayIcon.displayMessage(caption, text, TrayIcon.MessageType.NONE);
            } catch (IOException | AWTException e) {
                e.printStackTrace();
            }
        }
    }
    
}
