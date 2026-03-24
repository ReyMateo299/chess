package client;

import ui.*;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade server;
    private UI currUI;
//    private State state = State.PRELOGIN;

    public Client(String serverUrl) throws Exception {
        server = new ServerFacade(serverUrl);
        currUI = new PreloginUI(server);
    }

    public void run() {
        System.out.println("♕ Welcome to 240 Chess! Type help to get started");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = currUI.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
