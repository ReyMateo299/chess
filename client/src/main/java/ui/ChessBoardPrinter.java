package ui;

import static ui.EscapeSequences.*;

public class ChessBoardPrinter {

    public static String printChessBoard(String color) {
        StringBuilder sb = new StringBuilder();
        sb.append(printLetterRow(color)).append(nextLine());

        if (color.equals("WHITE")) {
            sb.append(printCheckersWhite());
        } else {
            sb.append(printCheckersBlack());
        }

        sb.append(printLetterRow(color)).append(nextLine());
        return sb.toString();
    }

    private static String printCheckersWhite() {

        return SET_BORDER_CONFIGS + " 8 " +
                SET_TEXT_COLOR_BLUE + printLastRowCheckers("WHITE", "WHITE") +
                SET_BORDER_CONFIGS + " 8 " + nextLine() +

                SET_BORDER_CONFIGS + " 7 " +
                SET_TEXT_COLOR_BLUE + printRow("BLACK", " P ") +
                SET_BORDER_CONFIGS + " 7 " + nextLine() +

                printEmptyRows(new String[]{" 6 ", " 5 ", " 4 ", " 3 "}) +

                SET_BORDER_CONFIGS + " 2 " +
                SET_TEXT_COLOR_RED + printRow("WHITE", " P ") +
                SET_BORDER_CONFIGS + " 2 " + nextLine() +

                SET_BORDER_CONFIGS + " 1 " +
                SET_TEXT_COLOR_RED + printLastRowCheckers("BLACK", "WHITE") +
                SET_BORDER_CONFIGS + " 1 " + nextLine();
    }

    private static String printCheckersBlack() {

        return SET_BORDER_CONFIGS + " 1 " +
                SET_TEXT_COLOR_RED + printLastRowCheckers("WHITE", "BLACK") +
                SET_BORDER_CONFIGS + " 1 " + nextLine() +

                SET_BORDER_CONFIGS + " 2 " +
                SET_TEXT_COLOR_RED + printRow("BLACK", " P ") +
                SET_BORDER_CONFIGS + " 2 " + nextLine() +

                printEmptyRows(new String[]{" 3 ", " 4 ", " 5 ", " 6 "}) +

                SET_BORDER_CONFIGS + " 7 " +
                SET_TEXT_COLOR_BLUE + printRow("WHITE", " P ") +
                SET_BORDER_CONFIGS + " 7 " + nextLine() +

                SET_BORDER_CONFIGS + " 8 " +
                SET_TEXT_COLOR_BLUE + printLastRowCheckers("BLACK", "BLACK") +
                SET_BORDER_CONFIGS + " 8 " + nextLine();
    }

    private static String printEmptyRows(String[] rows) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        String currTile = "WHITE";
        while (i < 4) {
            sb.append(SET_BORDER_CONFIGS).append(rows[i]);
            sb.append(printRow(currTile, "   "));
            sb.append(SET_BORDER_CONFIGS).append(rows[i]).append(nextLine());
            currTile = swapTile(currTile);
            i++;
        }
        return sb.toString();
    }

    private static String printLetterRow(String color) {
        if (color.equals("WHITE")) {
            return SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + "    a  b  c  d  e  f  g  h    ";
        }
        return SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + "    h  g  f  e  d  c  b  a    ";
    }

    private static String printRow(String startingTile, String tile) {
        String currTile = startingTile;
        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < 8) {
            if (currTile.equals("BLACK")) {
                result.append(SET_BG_COLOR_BLACK);
                result.append(tile);
                currTile = "WHITE";
            } else {
                result.append(SET_BG_COLOR_WHITE);
                result.append(tile);
                currTile = "BLACK";
            }
            i++;
        }
        return result.toString();
    }

    private static String printLastRowCheckers(String startingTile, String color) {
        String currTile = startingTile;
        StringBuilder result = new StringBuilder();
        String[] tiles;
        if (color.equals("WHITE")) {
            tiles = new String[]{" R ", " N ", " B ", " Q ", " K ", " B ", " N ", " R "};
        } else {
            tiles = new String[]{" R ", " N ", " B ", " K ", " Q ", " B ", " N ", " R "};
        }

        int i = 0;

        while (i < 8) {
            if (currTile.equals("BLACK")) {
                result.append(SET_BG_COLOR_BLACK);
                result.append(tiles[i]);
                currTile = "WHITE";
            } else {
                result.append(SET_BG_COLOR_WHITE);
                result.append(tiles[i]);
                currTile = "BLACK";
            }
            i++;
        }
        return result.toString();
    }

    private static String swapTile(String currTile) {
        if (currTile.equals("BLACK")) {
            return "WHITE";
        } else {
            return "BLACK";
        }
    }

    private static String nextLine() {
        return RESET_BG_COLOR + RESET_TEXT_COLOR + "\n";
    }
}
