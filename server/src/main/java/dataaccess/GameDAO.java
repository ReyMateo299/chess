package dataaccess;

import chess.ChessMove;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    Collection<GameData> listGames() throws DataAccessException;

    GameData createGame(String gameName) throws DataAccessException;

    GameData updateGame(int gameID, String userName, String playerColor) throws DataAccessException;

    GameData removePlayer(int gameID, String playerColor) throws DataAccessException;

    GameData updateGameWithMove(int gameID, ChessMove move) throws DataAccessException;

    GameData getGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void clear() throws DataAccessException;
}
