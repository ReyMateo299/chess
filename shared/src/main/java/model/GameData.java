package model;

import chess.ChessGame;

public record GameData(
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName,
        ChessGame game) {

    public GameData addPlayer(String playerColor, String userName) {
        if (playerColor.equals("WHITE")) {
            return new GameData(this.gameID, userName, this.blackUsername, this.gameName, this.game);
        } else if (playerColor.equals("BLACK")) {
            return new GameData(this.gameID, this.whiteUsername, userName, this.gameName, this.game);
        }
        return null;
    }
}
