package ui;

import client.ServerFacade;
import client.State;

import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class GameplayUI {
    private final ServerFacade server;
    private State nextState;

    public GameplayUI(ServerFacade server) {
        this.server = server;
    }

    public State run() {
        System.out.println(RESET_TEXT_COLOR + "👑 Welcome to 240 Chess! Type help to get started. 👑");

        Scanner scanner = new Scanner(System.in);
        var result = "";

        nextState = State.PRELOGIN;
        while (nextState == State.PRELOGIN) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        return nextState;
    }
}
