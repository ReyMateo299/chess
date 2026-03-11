package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;

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

    public AuthData createAuth(String username) {
        return new AuthData("authToken", "username");
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
}
