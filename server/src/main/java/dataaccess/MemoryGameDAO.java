package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private int nextId = 1;
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public GameData createGame(String gameName) {
        GameData newGame = new GameData(
                nextId, null, null, gameName, new ChessGame());
        games.put(nextId++, newGame);
        return newGame;
    }

    public GameData getGame(String gameName) {
        for (GameData gameData : games.values()) {
            if (gameData.gameName().equals(gameName)) {
                return gameData;
            }
        }
        return null;
    }

    public Collection<GameData> listGames() {
        return games.values();
    }

    public void clear() {
        games.clear();
    };
}
