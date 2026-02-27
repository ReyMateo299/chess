package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    final private HashMap<Integer, GameData> games = new HashMap<>();

//    public GameData getGame() {}

    public Collection<GameData> listGames() {
        return games.values();
    }

    public void clear() {
        games.clear();
    };
}
