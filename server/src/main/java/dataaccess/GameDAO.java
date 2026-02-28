package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    Collection<GameData> listGames();

    GameData createGame(String gameName);

    GameData updateGame(int gameID, String userName, String playerColor);

    GameData getGame(String gameName);

    GameData getGame(int gameID);

    void clear();
}
