package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws DataAccessException {
        String createStatement = """
                CREATE TABLE IF NOT EXISTS  games (
                    `id` int NOT NULL AUTO_INCREMENT,
                    `whiteUsername` varchar(255) DEFAULT NULL,
                    `blackUsername` varchar(255) DEFAULT NULL,
                    `gameName` varchar(255) NOT NULL UNIQUE,
                    `game` TEXT NOT NULL,
                    PRIMARY KEY (`id`),
                    INDEX(id),
                    INDEX(gameName)
                )
                """;
        DatabaseManager.configureDatabase(createStatement);
    }

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

    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String createStatement = "truncate table games";
            try (var preparedStatement = conn.prepareStatement(createStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to delete game data: %s", ex);
        }
    };
}
