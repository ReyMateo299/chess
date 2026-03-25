package client;

import ui.*;

import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class Client {
    private final ServerFacade server;
    private final PreloginUI preloginUI;
    private final PostloginUI postloginUI;
    private final GameplayUI gameplayUI;
    private State state;
    private String authToken;

    public Client(String serverUrl) throws Exception {
        server = new ServerFacade(serverUrl);
        state = State.PRELOGIN;
        preloginUI = new PreloginUI(server);
        postloginUI = new PostloginUI(server);
        gameplayUI = new GameplayUI(server);
        authToken = null;
    }

    public void run() {
        System.out.println(RESET_TEXT_COLOR + "👑 Welcome to 240 Chess! Type help to get started. 👑");

        UIResult result = new UIResult("", state, null);
        while (state != State.QUIT) {
            switch (state) {
                case PRELOGIN -> result = preloginUI.run();
                case POSTLOGIN -> result = postloginUI.run(authToken);
                case GAMEPLAY -> result = gameplayUI.run();
            }
            updateVariables(result);
        }
        System.out.println(result);
    }

    private void updateVariables(UIResult result) {
        state = result.nextState();

        if (authToken == null && result.authToken() != null) {
            authToken = result.authToken();
        }
    }
}
