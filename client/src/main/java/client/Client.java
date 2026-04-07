package client;

import ui.*;
import client.websocket.WebSocketFacade;
import websocket.commands.UserGameCommand;

import static ui.EscapeSequences.*;

public class Client {
    private final String serverUrl;
    private final ServerFacade server;
    private final PreloginUI preloginUI;
    private final PostloginUI postloginUI;
    private final GameplayUI gameplayUI;

    private WebSocketFacade ws;
    private State state;
    private String authToken;

    public Client(String serverUrl) throws Exception {
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
        ws = null;
        state = State.PRELOGIN;
        preloginUI = new PreloginUI(server);
        postloginUI = new PostloginUI(server);
        gameplayUI = new GameplayUI(server);
        authToken = null;
    }

    public void run() {
        System.out.println(RESET_TEXT_COLOR + "👑 Welcome to 240 Chess! Type " + SET_TEXT_COLOR_BLUE +
                "help" + RESET_TEXT_COLOR + " to get started. 👑");

        UIResult result = new UIResult("", state, null, null);
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
        OpenWebsocket openWebsocket = result.openWebsocket();
        if (openWebsocket.open() == true) {
            initiateGameplay(openWebsocket);
        }
    }

    private void initiateGameplay(OpenWebsocket openWebsocket) {
        try {
            ws = new WebSocketFacade(serverUrl);
            ws.sendCommand(new UserGameCommand(
                    UserGameCommand.CommandType.CONNECT, authToken, openWebsocket.gameID()));
        } catch (ResponseException e) {
            System.out.println("Error connecting to game.");
        }
    }
}
