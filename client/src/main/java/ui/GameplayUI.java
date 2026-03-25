package ui;

import client.ResponseException;
import client.ServerFacade;
import client.State;
import requests.CreateGameRequest;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayUI {
    private final ServerFacade server;
    private Scanner scanner;

    public GameplayUI(ServerFacade server) {
        this.server = server;
        this.scanner = new Scanner(System.in);
    }

    public UIResult run() {
        printPrompt();
        String line = scanner.nextLine();
        UIResult uiResult = eval(line);
        System.out.print(SET_TEXT_COLOR_BLUE + uiResult.message());
        return uiResult;
    }

    public UIResult eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
//                case "observe" -> observeGame(params);
            case "quit" -> quit();
            default -> help();
        };
    }

    private UIResult quit() {
        String message = "Thanks for playing! Exiting the application...";
        return new UIResult(message, State.GAMEPLAY, null);
    }

    private UIResult help() {
        String message =  """
                - Change this
                """;
        return new UIResult(message, State.GAMEPLAY, null);
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_IN] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
