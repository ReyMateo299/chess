package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {

    public GameData createGame(String gameName) {
        return new GameData(1, null, null, null, new ChessGame());
    }

    public GameData updateGame(int gameID, String userName, String playerColor) {
        return new GameData(1, null, null, null, new ChessGame());
    }

    public GameData getGame(String gameName) {
        return new GameData(1, null, null, null, new ChessGame());
    }

    public GameData getGame(int gameID) {
        return new GameData(1, null, null, null, new ChessGame());
    }

    public Collection<GameData> listGames() {
        // Implement listGames here
        return new ArrayList<>();
    }

    public void clear() {
        // Implement clear
    };
}
