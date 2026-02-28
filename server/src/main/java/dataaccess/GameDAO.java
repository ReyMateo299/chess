package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    Collection<GameData> listGames();

    GameData createGame(String gameName);

    GameData getGame(String gameName);

    void clear();
}
