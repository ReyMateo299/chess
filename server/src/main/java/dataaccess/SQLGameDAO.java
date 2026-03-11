package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

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

    public GameData createGame(String gameName) throws DataAccessException {

        try (var conn = DatabaseManager.getConnection()) {
            conn.setCatalog("chess");
            var statement = "INSERT INTO games (gameName, game) VALUES (?, ?)";
            ChessGame game = new ChessGame();
            try (var preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, gameName);
                preparedStatement.setString(2, new Gson().toJson(game));
                preparedStatement.executeUpdate();

                int id;
                ResultSet rs = preparedStatement.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                } else {
                    id = 0;
                }

                return new GameData(
                        id, null, null, gameName, game);
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Unable to create game");
        }
    }

    public GameData updateGame(int gameID, String userName, String playerColor) {
        return new GameData(1, null, null, null, new ChessGame());
    }

    public GameData getGame(String gameName) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            conn.setCatalog("chess");

            var statement = """
                        SELECT id, whiteUsername, blackUsername, gameName, game
                        FROM games WHERE gameName = ?
                    """;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, gameName);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        var id = rs.getInt("id");
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("whiteUsername");
                        var gameString = rs.getString("game");
                        ChessGame game = new Gson().fromJson(gameString, ChessGame.class);
                        return new GameData(id, whiteUsername, blackUsername, gameName, game);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to access authData table");
        }

        return null;
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
