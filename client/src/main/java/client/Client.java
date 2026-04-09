package client;

import client.websocket.ServerMessageHandler;
import ui.*;
import client.websocket.WebSocketFacade;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import static ui.EscapeSequences.*;

public class Client implements ServerMessageHandler {
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
        if (openWebsocket != null && openWebsocket.open() == true) {
            initiateGameplay(openWebsocket);
        }
    }

    private void initiateGameplay(OpenWebsocket openWebsocket) {
        try {
            ws = new WebSocketFacade(serverUrl, this);
            ws.sendCommand(new UserGameCommand(
                    UserGameCommand.CommandType.CONNECT, authToken, openWebsocket.gameID()));
        } catch (ResponseException e) {
            System.out.println("Error connecting to game.");
        }
    }

    public void printLoadGame(LoadGameMessage message) {
        String serializedGame = message.getGame();
        System.out.println(SET_TEXT_COLOR_RED + "Load Message: " + serializedGame);
//        System.out.println(ChessBoardPrinter.printChessBoard("WHITE"));
        printGameplayUIPrompt();
    }

    public void printErrorMessage(ErrorMessage message) {
        System.out.println(SET_TEXT_COLOR_RED + "Error Message: " + message.getErrorMessage());
        printGameplayUIPrompt();
    }

    public void notify(NotificationMessage notification) {
        System.out.println(SET_TEXT_COLOR_RED + notification.getMessage());
        printGameplayUIPrompt();
    }

    private void printGameplayUIPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[IN_GAME] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
