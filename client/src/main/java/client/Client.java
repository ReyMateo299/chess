package client;

import ui.*;
import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade server;

    public Client(String serverUrl) throws Exception {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        PreloginUI.run();
    }

    public static void printPrompt() {
        System.out.print("\n" +  ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
