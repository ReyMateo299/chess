package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() throws DataAccessException {
        configureDatabase();
    }

    public UserData getUser(String username) {
        return new UserData("user", "password", "email");
    }

    public UserData createUser(String username, String password, String email) {
        return new UserData("user", "password", "email");
    }

    public void clear() {
        // Implement clear
    }

    // Add INDEX(name)
    //            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

    /**
     * Creates Auth, Game, and User tables in the database.
     */
    public void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            String createStatement = """
                    CREATE TABLE IF NOT EXISTS  users (
                      `username` varchar(255) NOT NULL,
                      `password` varchar(255) NOT NULL,
                      `email` varchar(255) NOT NULL,
                      PRIMARY KEY (`username`)
                    )
                    """;
            try (var preparedStatement = conn.prepareStatement(createStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database: %s", ex);
        }
    }
}
