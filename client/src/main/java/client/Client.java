package client;

import ui.*;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade server;
    public State state;
    private PreloginUI preloginUI;
    private PostloginUI postloginUI;
    private GameplayUI gameplayUI;

    public Client(String serverUrl) throws Exception {
        server = new ServerFacade(serverUrl);
        state = State.PRELOGIN;
        preloginUI = new PreloginUI(server);
        postloginUI = new PostloginUI(server);
        gameplayUI = new GameplayUI(server);
    }

    public void run() {
        while (state != State.QUIT) {
            switch (state) {
                case PRELOGIN -> state = preloginUI.run();
                case POSTLOGIN -> state = postloginUI.run();
//                case GAMEPLAY -> state = gameplayUI.run();
            }
        }
        System.out.println();
    }
}
