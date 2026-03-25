package client;

import dataaccess.*;
import org.junit.jupiter.api.*;
import requests.*;
import results.*;
import server.Server;
import service.*;
import service.exceptions.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        String serverUrl = "http://localhost:" + port;
        facade = new ServerFacade(serverUrl);
    }

    @BeforeEach
    public void reset() throws Exception {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    @Order(1)
    @DisplayName("Register - Positive")
    public void registerSuccess() throws ResponseException {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        var authData = facade.register(request);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    @Order(2)
    @DisplayName("Register - Negative")
    public void registerTakenUsername() throws ResponseException {
        RegisterRequest request = new RegisterRequest("name", "password", "email");
        Assertions.assertDoesNotThrow(() -> facade.register(request));
        try {
            facade.register(request);
        } catch (ResponseException ex) {
            Assertions.assertEquals("Error: username already taken", ex.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("Login - Positive")
    public void loginSuccess() throws ResponseException {
        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest(
                "name", "password", "email")));

        LoginRequest loginRequest = new LoginRequest("name", "password");
        Assertions.assertDoesNotThrow(() -> facade.login(loginRequest));
    }

    @Test
    @Order(4)
    @DisplayName("Login - Negative")
    public void loginIncorrectPassword() throws ResponseException {
        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest(
                "name", "password", "email")));

        LoginRequest loginRequest = new LoginRequest("name", "wrongPassword");
        Assertions.assertThrows(ResponseException.class, () -> facade.login(loginRequest));
        try {
            facade.login(loginRequest);
        } catch (ResponseException ex) {
            Assertions.assertEquals("Error: invalid password", ex.getMessage());
        }
    }

    @Test
    @Order(5)
    @DisplayName("Logout - Positive")
    public void logoutSuccess() throws ResponseException {
        RegisterResult result = registerUser("name");

        LogoutRequest logoutRequest = new LogoutRequest(result.authToken());
        Assertions.assertDoesNotThrow(() -> facade.logout(logoutRequest));
    }

    @Test
    @Order(6)
    @DisplayName("Logout - Negative")
    public void logoutMissingAuthentication() throws ResponseException {
        LogoutRequest logoutRequest = new LogoutRequest("");
        Assertions.assertThrows(ResponseException.class, () -> facade.logout(logoutRequest));
    }

    @Test
    @Order(7)
    @DisplayName("Create Game - Positive")
    public void createGameSuccess() throws ResponseException {
        RegisterResult result = registerUser("name");

        CreateGameRequest createGameRequest = new CreateGameRequest(result.authToken(), "gameName");
        Assertions.assertDoesNotThrow(() -> facade.createGame(createGameRequest));
    }

    @Test
    @Order(8)
    @DisplayName("Create Game - Negative")
    public void createGameAlreadyTaken() throws ResponseException {
        RegisterResult result = registerUser("name");

        CreateGameRequest createGameRequest = new CreateGameRequest(result.authToken(), "gameName");
        Assertions.assertDoesNotThrow(() -> facade.createGame(createGameRequest));
        try {
            facade.createGame(createGameRequest);
        } catch (ResponseException ex) {
            Assertions.assertEquals("Error: game name already in use", ex.getMessage());
        }
    }

    @Test
    @Order(9)
    @DisplayName("List Games - Positive")
    public void listGamesSuccess() throws ResponseException {
        ListGamesResult listResult = null;
        try {
            listResult = setupListTest();
        } catch (ServiceException s) {
            Assertions.fail();
        }

        ListGamesResult expected = new ListGamesResult(List.of(new GameResult(
                1, null, null, "gameName"
        )));
        Assertions.assertEquals(listResult.games(), expected.games());
    }

    @Test
    @Order(10)
    @DisplayName("List Games - Negative (Empty fields are null not strings")
    public void listGamesFailure() throws ResponseException {
        ListGamesResult listResult = null;
        try {
            listResult = setupListTest();
        } catch (ServiceException s) {
            Assertions.fail();
        }

        ListGamesResult expectedFalse = new ListGamesResult(List.of(new GameResult(
                1, "", "", "gameName"
        )));
        Assertions.assertNotEquals(listResult.games(), expectedFalse.games());
    }

    @Test
    @Order(11)
    @DisplayName("Join Game - Positive")
    public void joinGameSuccess() throws ResponseException {
        RegisterResult result = registerUser("name");

        Assertions.assertDoesNotThrow(() -> createGame(result.authToken()));

        JoinGameRequest joinRequest = new JoinGameRequest(result.authToken(), "WHITE", 1);
        Assertions.assertDoesNotThrow(() -> facade.joinGame(joinRequest));
    }

    @Test
    @Order(12)
    @DisplayName("Join Game - Negative")
    public void joinGameColorTaken() throws ResponseException {
        RegisterResult result = registerUser("name");

        Assertions.assertDoesNotThrow(() -> createGame(result.authToken()));

        JoinGameRequest joinRequest = new JoinGameRequest(result.authToken(), "WHITE", 1);
        Assertions.assertDoesNotThrow(() -> facade.joinGame(joinRequest));

        JoinGameRequest badRequest = new JoinGameRequest(result.authToken(), "WHITE", 1);

        try {
            facade.joinGame(badRequest);
        } catch (ResponseException ex) {
            Assertions.assertEquals("Error: team already taken", ex.getMessage());
        }
    }

    @Test
    @Order(13)
    @DisplayName("Clear - Positive")
    public void clearServiceSuccess() {
        try {
            facade.clear();
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    public ListGamesResult setupListTest() throws ServiceException, ResponseException {
        RegisterRequest request = new RegisterRequest("name", "password", "email");
        RegisterResult result = facade.register(request);

        Assertions.assertDoesNotThrow(() -> createGame(result.authToken()));

        ListGamesRequest listRequest = new ListGamesRequest(result.authToken());
        return facade.listGames(listRequest);
    }

    private RegisterResult registerUser(String username) throws ResponseException {
        return facade.register(new RegisterRequest(username, "password", "email"));
    }

    private void createGame(String token) throws ResponseException {
        facade.createGame(new CreateGameRequest(token, "gameName"));
    }
}
