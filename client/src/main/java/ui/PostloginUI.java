package ui;

import client.Client;
import client.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PostloginUI implements UI {
    private final ServerFacade server;

    public PostloginUI(ServerFacade server) {
        this.server = server;
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
}
