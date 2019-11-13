package de.thu.inf.spro.chattitude.backend;

public class Main {
    public static void main(String[] args) {
        System.out.println("  ______  __    __       ___   .___________.___________. __  .___________. __    __   _______   _______ \n" +
                " /      ||  |  |  |     /   \\  |           |           ||  | |           ||  |  |  | |       \\ |   ____|\n" +
                "|  ,----'|  |__|  |    /  ^  \\ `---|  |----`---|  |----`|  | `---|  |----`|  |  |  | |  .--.  ||  |__   \n" +
                "|  |     |   __   |   /  /_\\  \\    |  |        |  |     |  |     |  |     |  |  |  | |  |  |  ||   __|  \n" +
                "|  `----.|  |  |  |  /  _____  \\   |  |        |  |     |  |     |  |     |  `--'  | |  '--'  ||  |____ \n" +
                " \\______||__|  |__| /__/     \\__\\  |__|        |__|     |__|     |__|      \\______/  |_______/ |_______|\n" +
                "                                                                                                        ");
        System.out.println("Starting ChattitudeServer…");
        var server = new Server();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down ChattitudeServer…");
            server.close();
        }));
    }
}
