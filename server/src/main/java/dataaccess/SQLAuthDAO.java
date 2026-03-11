package dataaccess;

import com.google.gson.Gson;
import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws DataAccessException {
        String createStatement = """
                CREATE TABLE IF NOT EXISTS  auth (
                    `authToken` varchar(255) NOT NULL,
                    `username` varchar(255) NOT NULL,
                    `json` TEXT DEFAULT NULL,
                    PRIMARY KEY (`authToken`),
                    INDEX(authToken),
                    INDEX(username)
                )
                """;
        DatabaseManager.configureDatabase(createStatement);
    }

    public AuthData createAuth(String username) throws DataAccessException {
        String authToken = generateToken();
        AuthData newAuth = new AuthData(authToken, username);

        try (var conn = DatabaseManager.getConnection()) {
            conn.setCatalog("chess");
            var statement = "INSERT INTO auth (authToken, username, json) VALUES (?, ?, ?)";
            String json = new Gson().toJson(newAuth);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                preparedStatement.setString(2, username);
                preparedStatement.setString(3, json);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to create authToken");
        }

        return newAuth;
    }

    public AuthData getAuth(String authToken) {
        return new AuthData("authToken", "username");
    }

    public boolean deleteAuth(String authToken) {
        return false;
    }

    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String createStatement = "truncate table auth";
            try (var preparedStatement = conn.prepareStatement(createStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to delete auth data: %s", ex);
        }
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
