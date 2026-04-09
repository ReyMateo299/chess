package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
//            conn.setCatalog("chess");
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

    public GameData updateGame(int gameID, String userName, String playerColor) throws DataAccessException {
        GameData gameData = getGame(gameID);
        if (playerColor.equals("WHITE") && gameData.whiteUsername() != null) {
            return null;
        }
        if (playerColor.equals("BLACK") && gameData.blackUsername() != null) {
            return null;
        }
        GameData updatedGame = gameData.addPlayer(playerColor, userName);

        try (var conn = DatabaseManager.getConnection()) {
//            conn.setCatalog("chess");
            var statement = "";
            if (playerColor.equals("WHITE")) {
                statement = "UPDATE games SET whiteUsername = ?  WHERE id = ?";
            } else {
                statement = "UPDATE games SET blackUsername = ?  WHERE id = ?";
            }
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, userName);
                preparedStatement.setInt(2, gameID);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to update games table");
        }
        return updatedGame;

    }

    public void updateGameWithMove(int gameID, ChessMove move) throws DataAccessException {
        GameData gameData = getGame(gameID);
        ChessGame chessGame = gameData.game();

        try {
            chessGame.makeMove(move);
        } catch (InvalidMoveException e) {
            throw new DataAccessException(e.getMessage());
        }

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE games SET game = ?  WHERE id = ?";
            try (var preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, new Gson().toJson(chessGame));
                preparedStatement.setInt(2, gameID);

                preparedStatement.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Unable to create game");
        }
    }

    public void endGame(int gameID) throws DataAccessException {
        GameData gameData = getGame(gameID);
        ChessGame chessGame = gameData.game();
        chessGame.setTeamTurn(null);
        Gson gsonWithNulls = new GsonBuilder().serializeNulls().create();

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE games SET game = ?  WHERE id = ?";
            try (var preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, gsonWithNulls.toJson(chessGame));
                preparedStatement.setInt(2, gameID);

                preparedStatement.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Unable to create game");
        }
    }

    public void removePlayer(int gameID, String playerColor) throws DataAccessException {
        GameData gameData = getGame(gameID);
//        if (playerColor.equals("WHITE") && gameData.whiteUsername() == null) {
//            return null;
//        }
//        if (playerColor.equals("BLACK") && gameData.blackUsername() != null) {
//            return null;
//        }
        GameData updatedGame = gameData.addPlayer(playerColor, null);

        try (var conn = DatabaseManager.getConnection()) {
//            conn.setCatalog("chess");
            var statement = "";
            if (playerColor.equals("WHITE")) {
                statement = "UPDATE games SET whiteUsername = ?  WHERE id = ?";
            } else {
                statement = "UPDATE games SET blackUsername = ?  WHERE id = ?";
            }
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, null);
                preparedStatement.setInt(2, gameID);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to update games table");
        }

    }

    public GameData getGame(String gameName) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
//            conn.setCatalog("chess");

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
            throw new DataAccessException("Unable to access games table");
        }

        return null;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
//            conn.setCatalog("chess");

            var statement = """
                        SELECT id, whiteUsername, blackUsername, gameName, game
                        FROM games WHERE id = ?
                    """;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");
                        var gameString = rs.getString("game");
                        ChessGame game = new Gson().fromJson(gameString, ChessGame.class);
                        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to access games table");
        }

        return null;
    }

    public Collection<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        var id = rs.getInt("id");
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");
                        var gameString = rs.getString("game");
                        ChessGame game = new Gson().fromJson(gameString, ChessGame.class);
                        result.add(new GameData(id, whiteUsername, blackUsername, gameName, game));
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to list game data: %s", ex);
        }

        return result;
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
    }
}
