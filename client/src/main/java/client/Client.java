package client;

import ui.*;
import client.websocket.WebSocketFacade;

import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade server;
    private final WebSocketFacade ws;
    private final PreloginUI preloginUI;
    private final PostloginUI postloginUI;
    private final GameplayUI gameplayUI;
    private State state;
    private String authToken;

    public Client(String serverUrl) throws Exception {
        server = new ServerFacade(serverUrl);
        // Maybe have this ws creation happen later?
        ws = new WebSocketFacade(serverUrl);
        state = State.PRELOGIN;
        preloginUI = new PreloginUI(server);
        postloginUI = new PostloginUI(server);
        gameplayUI = new GameplayUI(server);
        authToken = null;
    }

    public void run() {
        System.out.println(RESET_TEXT_COLOR + "👑 Welcome to 240 Chess! Type " + SET_TEXT_COLOR_BLUE +
                "help" + RESET_TEXT_COLOR + " to get started. 👑");

        UIResult result = new UIResult("", state, null);
        while (state != State.QUIT) {
            switch (state) {
                case PRELOGIN -> result = preloginUI.run();
                case POSTLOGIN -> result = postloginUI.run(authToken);
                case GAMEPLAY -> result = gameplayUI.run(authToken);
            }
            updateVariables(result);
        }
        System.out.println();
    }

    private void updateVariables(UIResult result) {
        state = result.nextState();
        authToken = result.authToken();
    }
}
