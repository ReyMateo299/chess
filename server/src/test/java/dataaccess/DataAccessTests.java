package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import service.exceptions.*;
import requests.*;
import results.GameResult;
import results.ListGamesResult;
import results.RegisterResult;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DataAccessTests {

    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    private static UserDAO userDAO;

    @BeforeAll
    public static void init() throws DataAccessException {
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();
        userDAO = new SQLUserDAO();
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Get User - Positive")
    public void getUserSuccess() {
        Assertions.assertDoesNotThrow(() -> userDAO.getUser("username"));
    }

    @Test
    @Order(2)
    @DisplayName("Get User - Negative")
    public void getUserInvalidUsername() throws DataAccessException {
        UserData createdUser = userDAO.createUser("username", "password", "email");
        UserData emptyUser = userDAO.getUser("wrongUsername");
        Assertions.assertNotEquals(createdUser, emptyUser);
    }

    @Test
    @Order(3)
    @DisplayName("Create User - Positive")
    public void createUserSuccess() throws DataAccessException {
        UserData createdUser = userDAO.createUser("username", "password", "my@email");
        UserData retrievedUser = userDAO.getUser("username");
        Assertions.assertEquals(createdUser.email(), retrievedUser.email());
    }

    @Test
    @Order(4)
    @DisplayName("Create User - Username Taken")
    public void createUserNegative() throws DataAccessException {
        userDAO.createUser("myUsername", "password", "my@email");
        Assertions.assertThrows(DataAccessException.class,
                () -> userDAO.createUser("myUsername", "password", "my@email")
        );
    }

    @Test
    @Order(5)
    @DisplayName("User Clear - Positive")
    public void userClearPositive() throws DataAccessException {
        userDAO.createUser("myUsername", "password", "my@email");
        userDAO.clear();
        UserData userData = userDAO.getUser("myUsername");
        Assertions.assertEquals(null, userData);
    }

    @Test
    @Order(6)
    @DisplayName("Create Auth - Success")
    public void createAuthPositive() throws DataAccessException {
        AuthData testAuth = authDAO.createAuth("myUsername");
        AuthData retrievedAuth = authDAO.getAuth(testAuth.authToken());
        Assertions.assertEquals(testAuth, retrievedAuth);
    }

    @Test
    @Order(7)
    @DisplayName("Create Auth - Auth Tokens Not Same")
    public void createAuthNegative() throws DataAccessException {
        AuthData testAuth1 = authDAO.createAuth("myUsername");
        AuthData testAuth2 = authDAO.createAuth("otherUsername");
        Assertions.assertNotEquals(testAuth2.authToken(), testAuth1.authToken());
    }

    @Test
    @Order(8)
    @DisplayName("Get Auth - Success")
    public void getAuthPositive() throws DataAccessException {
        authDAO.createAuth("myUsername");
        Assertions.assertDoesNotThrow(() -> authDAO.getAuth("myUsername"));
    }

    @Test
    @Order(9)
    @DisplayName("Get Auth - Auth Token Not Exist")
    public void getAuthNegative() throws DataAccessException {
        authDAO.createAuth("myUsername");
        AuthData emptyAuth = authDAO.getAuth("wrongUsername");
        Assertions.assertNull(emptyAuth);
    }

    @Test
    @Order(10)
    @DisplayName("Delete Auth - Success")
    public void deleteAuthPositive() throws DataAccessException {
        authDAO.createAuth("myUsername");
        authDAO.deleteAuth("myUsername");
        Assertions.assertNull(authDAO.getAuth("myUsername"));
    }

    @Test
    @Order(11)
    @DisplayName("Delete Auth - Auth Not Exist")
    public void deleteAuthNegative() throws DataAccessException {
        authDAO.createAuth("myUsername");
        Assertions.assertFalse(authDAO.deleteAuth("wrongUsername"));
    }

    @Test
    @Order(12)
    @DisplayName("Auth Clear - Success")
    public void authClearPositive() throws DataAccessException {
        authDAO.createAuth("myUsername");
        authDAO.createAuth("otherUsername");
        authDAO.clear();
        AuthData authData = authDAO.getAuth("myUsername");
        Assertions.assertNull(authData);
    }

    @Test
    @Order(13)
    @DisplayName("Create Game - Success")
    public void createGamePositive() throws DataAccessException {
        GameData testGame = gameDAO.createGame("myGame");
        GameData retrievedGame = gameDAO.getGame("myGame");
        Assertions.assertEquals(testGame.gameID(), retrievedGame.gameID());
    }

    @Test
    @Order(14)
    @DisplayName("Create Game - Game ID Not Same")
    public void createGameNegative() throws DataAccessException {
        GameData testGame1 = gameDAO.createGame("myGame");
        GameData testGame2 = gameDAO.createGame("otherGame");
        Assertions.assertNotEquals(testGame1.gameID(), testGame2.gameID());
    }

    @Test
    @Order(15)
    @DisplayName("Get Game - Success")
    public void getGamePositive() throws DataAccessException {
        gameDAO.createGame("myGame");
        Assertions.assertDoesNotThrow(() -> gameDAO.getGame("myGame"));
    }

    @Test
    @Order(16)
    @DisplayName("Get Game - Game Not Exist")
    public void getGameNegative() throws DataAccessException {
        gameDAO.createGame("myGame");
        GameData emptyGame = gameDAO.getGame("wrongName");
        Assertions.assertNull(emptyGame);
    }

    @Test
    @Order(17)
    @DisplayName("Get Game By ID - Success")
    public void getGameIDPositive() throws DataAccessException {
        gameDAO.createGame("myGame");
        Assertions.assertDoesNotThrow(() -> gameDAO.getGame(1));
    }

    @Test
    @Order(18)
    @DisplayName("Get Game By ID - Game Not Exist")
    public void getGameIDNegative() throws DataAccessException {
        gameDAO.createGame("myGame");
        int wrongNum = 2;
        GameData emptyGame = gameDAO.getGame(wrongNum);
        Assertions.assertNull(emptyGame);
    }

    @Test
    @Order(19)
    @DisplayName("Update Game - Success")
    public void updateGamePositive() throws DataAccessException {
        gameDAO.createGame("myGame");
        GameData updatedGame = gameDAO.updateGame(1, "myUsername", "WHITE");
        Assertions.assertEquals("myUsername", updatedGame.whiteUsername());
    }

    @Test
    @Order(20)
    @DisplayName("Update Game - Not Change Other Team")
    public void updateGameNegative() throws DataAccessException {
        gameDAO.createGame("myGame");
        GameData updatedGame = gameDAO.updateGame(1, "myUsername", "WHITE");
        Assertions.assertNull(updatedGame.blackUsername());
    }

    @Test
    @Order(21)
    @DisplayName("List Games - Success")
    public void listGamesPositive() throws DataAccessException {
        gameDAO.createGame("game1");
        gameDAO.createGame("game2");
        gameDAO.createGame("game3");
        Collection<GameData> games = gameDAO.listGames();

        int i = 1;
        for (GameData game : games) {
            Assertions.assertEquals(i, game.gameID());
            i++;
        }
    }

    @Test
    @Order(22)
    @DisplayName("List Games - ID Not Stay Same")
    public void listGamesNegative() throws DataAccessException {
        gameDAO.createGame("game1");
        gameDAO.createGame("game2");
        gameDAO.createGame("game3");
        Collection<GameData> games = gameDAO.listGames();

        ArrayList<GameData> gamesArray = new ArrayList<>(games);

        Assertions.assertNotEquals(1, gamesArray.get(1).gameID());
        Assertions.assertNotEquals(1, gamesArray.get(2).gameID());
    }

    @Test
    @Order(23)
    @DisplayName("Game Clear - Success")
    public void gameClearPositive() throws DataAccessException {
        gameDAO.createGame("myGame");
        gameDAO.createGame("otherGame");
        gameDAO.clear();
        GameData gameData = gameDAO.getGame("myGame");
        Assertions.assertNull(gameData);
    }
}
