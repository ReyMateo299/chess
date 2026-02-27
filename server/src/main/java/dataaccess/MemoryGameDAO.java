package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    final private HashMap<Integer, GameData> games = new HashMap<>();

//    public GameData getGame() {}

    public Collection<GameData> listGames() {
        games.put(1, new GameData(1,
                "matt",
                "luke",
                "game",
                new ChessGame()
                ));
        return games.values();
    }

    public void clear() {
        games.clear();
    };
}
