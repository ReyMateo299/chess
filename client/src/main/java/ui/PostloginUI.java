package ui;

import client.Client;
import client.ServerFacade;
import client.State;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PostloginUI implements UI {
    private final ServerFacade server;

    public PostloginUI(ServerFacade server) {
        this.server = server;
    }

    public State run() {
        System.out.println("♕ Welcome to 240 Chess! Type help to get started");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
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
        return State.QUIT;
    }

    public String eval(String input) {
//        try {
//            String[] tokens = input.toLowerCase().split(" ");
//            String cmd = (tokens.length > 0) ? tokens[0] : "help";
//            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
//            return switch (cmd) {
//                case "signin" -> signIn(params);
//                case "rescue" -> rescuePet(params);
//                case "list" -> listPets();
//                case "signout" -> signOut();
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
//                case "quit" -> "quit";
//                default -> help();
//            };
//        } catch (ResponseException ex) {
//            return ex.getMessage();
//        }
        if (input.equals("quit")) {
            return "quit";
        }
        return "Hello World";
    }

    private String help() {
        return """
                - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                - login <USERNAME> <PASSWORD> - to play chess
                - quit - playing chess
                - help - with possible commands
                """;
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
