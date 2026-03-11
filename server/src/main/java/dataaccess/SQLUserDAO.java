package dataaccess;

import com.google.gson.Gson;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() throws DataAccessException {
        String createStatement = """
                CREATE TABLE IF NOT EXISTS  users (
                    `username` varchar(255) NOT NULL,
                    `password` varchar(255) NOT NULL,
                    `email` varchar(255) NOT NULL,
                    `json` TEXT DEFAULT NULL,
                    PRIMARY KEY (`username`),
                    INDEX(username)
                )
                """;
        DatabaseManager.configureDatabase(createStatement);
    }

    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            conn.setCatalog("chess");

            var statement = "SELECT username, json FROM users WHERE username = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        var json = rs.getString("json");
                        return new Gson().fromJson(json, UserData.class);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database");
        }

        return null;
    }

    public UserData createUser(String username, String password, String email) throws DataAccessException {
        UserData newUser = new UserData(username, password, email);

        try (var conn = DatabaseManager.getConnection()) {
            conn.setCatalog("chess");
            var statement = "INSERT INTO users (username, password, email, json) VALUES (?, ?, ?, ?)";
            String json = new Gson().toJson(newUser);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, email);
                preparedStatement.setString(4, json);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to create user");
        }

        return newUser;
    }

    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String createStatement = "truncate table users";
            try (var preparedStatement = conn.prepareStatement(createStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to delete user data: %s", ex);
        }
    }
}
