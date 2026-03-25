package client;

import ui.*;

import static ui.EscapeSequences.*;

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
        System.out.println(RESET_TEXT_COLOR + "👑 Welcome to 240 Chess! Type " + SET_TEXT_COLOR_BLUE +
                "help" + RESET_TEXT_COLOR + " to get started. 👑");

        System.out.println(printChessBoard("WHITE"));

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

    private String printChessBoard(String color) {
        StringBuilder sb = new StringBuilder();

        sb.append(printLetterRow(color));
        sb.append(nextLine());
        if (color.equals("WHITE")) {
            sb.append(printCheckersWhite());
        } else {
            sb.append(printCheckersBlack());
        }

        sb.append(printLetterRow(color));
        sb.append(nextLine());

        return sb.toString();
    }

    private String printCheckersWhite() {
        StringBuilder sb = new StringBuilder();

        sb.append(SET_BORDER_CONFIGS).append(" 8 ");
        sb.append(SET_TEXT_COLOR_BLUE).append(printLastRowCheckers("WHITE"));
        sb.append(SET_BORDER_CONFIGS).append(" 8 ").append(nextLine());

        sb.append(SET_BORDER_CONFIGS).append(" 7 ");
        sb.append(SET_TEXT_COLOR_BLUE).append(printPawns("BLACK"));
        sb.append(SET_BORDER_CONFIGS).append(" 7 ").append(nextLine());

        int i = 0;
        String[] rows = {" 6 ", " 5 ", " 4 ", " 3 "};
        String currTile = "WHITE";
        while (i < 4) {
            sb.append(SET_BORDER_CONFIGS).append(rows[i]);
            sb.append(printEmptyRow(currTile));
            sb.append(SET_BORDER_CONFIGS).append(rows[i]).append(nextLine());
            currTile = swapTile(currTile);
            i++;
        }

        sb.append(SET_BORDER_CONFIGS).append(" 2 ");
        sb.append(SET_TEXT_COLOR_RED).append(printPawns("WHITE"));
        sb.append(SET_BORDER_CONFIGS).append(" 2 ").append(nextLine());

        sb.append(SET_BORDER_CONFIGS).append(" 1 ");
        sb.append(SET_TEXT_COLOR_RED).append(printLastRowCheckers("BLACK"));
        sb.append(SET_BORDER_CONFIGS).append(" 1 ").append(nextLine());

        return sb.toString();
    }

    private String printCheckersBlack() {
        return "HELLO";
    }

    private String printLastRowCheckers(String startingTile) {
        String currTile = startingTile;
        StringBuilder result = new StringBuilder();
        String[] pieces = {"R", "N", "B", "Q", "K", "B", "N", "R"};
        int i = 0;

        while (i < 8) {
            if (currTile.equals("BLACK")) {
                result.append(SET_BG_COLOR_BLACK);
                result.append(pieces[i]);
                currTile = "WHITE";
            } else {
                result.append(SET_BG_COLOR_WHITE);
                result.append(pieces[i]);
                currTile = "BLACK";
            }
            i++;
        }
        return result.toString();
    }

    private String printLetterRow(String color) {
        if (color.equals("WHITE")) {
            return SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + "    a  b  c  d  e  f  g  h    ";
        }
        return SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + "    h  g  f  e  d  c  b  a    ";
    }

    private String printPawns(String startingTile) {
        String currTile = startingTile;
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < 8) {
            if (currTile.equals("BLACK")) {
                result.append(SET_BG_COLOR_BLACK + " P ");
                currTile = "WHITE";
            } else {
                result.append(SET_BG_COLOR_WHITE + " P ");
                currTile = "BLACK";
            }
            i++;
        }
        return result.toString();
    }

    private String printEmptyRow(String startingTile) {
        String currTile = startingTile;
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < 8) {
            if (currTile.equals("BLACK")) {
                result.append(SET_BG_COLOR_BLACK + "   ");
                currTile = "WHITE";
            } else {
                result.append(SET_BG_COLOR_WHITE + "   ");
                currTile = "BLACK";
            }
            i++;
        }
        return result.toString();
    }

    private String swapTile(String currTile) {
        if (currTile.equals("BLACK")) {
            return "WHITE";
        } else {
            return "BLACK";
        }
    }

    private String nextLine() {
        return RESET_BG_COLOR + RESET_TEXT_COLOR + "\n";
    }
}
