package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    Collection<GameData> listGames() throws DataAccessException;

    GameData createGame(String gameName) throws DataAccessException;

    GameData updateGame(int gameID, String userName, String playerColor) throws DataAccessException;

    GameData getGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void clear() throws DataAccessException;
}
