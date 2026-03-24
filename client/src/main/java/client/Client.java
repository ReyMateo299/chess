package client;

import ui.*;
import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade server;
    private final PreloginUI preloginUI;
    private final PostloginUI postloginUI;
    private State state = State.PRELOGIN;

    public Client(String serverUrl) throws Exception {
        server = new ServerFacade(serverUrl);
        preloginUI = new PreloginUI();
        postloginUI = new PostloginUI();
    }

    public void run() {

        preloginUI.run();
    }

//    public void enterPostlogin() {
//        postloginUI.run();
//    }

    public static void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
