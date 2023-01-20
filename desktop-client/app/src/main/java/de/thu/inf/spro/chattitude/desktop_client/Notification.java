package de.thu.inf.spro.chattitude.desktop_client;

import de.thu.inf.spro.chattitude.desktop_client.ui.controller.MainScreenController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public final class Notification {
    private static TrayIcon trayIcon;

    static {
        SystemTray systemTray = SystemTray.getSystemTray();

        URL url = Notification.class.getResource("/LogoPicRound.png");

        try {
            Image image = ImageIO.read(url);
            trayIcon = new TrayIcon(image);
            systemTray.add(trayIcon);
            trayIcon.setToolTip("Chattitude");
        } catch (IOException | AWTException e) {
            e.printStackTrace();
        }
    }
    
    private Notification(){}
    
    public static void send(String caption, String text){
        if(!MainScreenController.IS_FOCUSED){
            trayIcon.displayMessage(caption, text, TrayIcon.MessageType.NONE);
        }
    }
    
}
