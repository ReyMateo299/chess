package service;

import dataaccess.*;
import org.junit.jupiter.api.*;
import passoff.model.TestCreateRequest;
import passoff.model.TestUser;
import passoff.server.TestServerFacade;
import server.Server;
import service.exceptions.*;
import service.requests.*;
import service.results.*;

import java.util.Collection;
import java.util.List;

public class BasicServiceTests {

    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    private static UserDAO userDAO;


    @BeforeAll
    public static void init() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userDAO = new MemoryUserDAO();
    }

    @BeforeEach
    public void setup() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userDAO = new MemoryUserDAO();
    }

    @Test
    @Order(1)
    @DisplayName("Register - Positive")
    public void registerSuccess() {
        UserService service = new UserService(authDAO, userDAO);

        RegisterRequest request = new RegisterRequest("name", "password", "email");
        Assertions.assertDoesNotThrow(() -> service.register(request));
    }

    @Test
    @Order(2)
    @DisplayName("Register - Negative")
    public void registerTakenUsername() {
        UserService service = new UserService(authDAO, userDAO);

        RegisterRequest request = new RegisterRequest("name", "password", "email");
        Assertions.assertDoesNotThrow(() -> service.register(request));
        Assertions.assertThrows(AlreadyTakenException.class, () -> service.register(request));
    }

    @Test
    @Order(3)
    @DisplayName("Login - Positive")
    public void loginSuccess() {
        UserService service = new UserService(authDAO, userDAO);

        Assertions.assertDoesNotThrow(() -> service.register(new RegisterRequest(
                "name", "password", "email")));

        LoginRequest loginRequest = new LoginRequest("name", "password");
        Assertions.assertDoesNotThrow(() -> service.login(loginRequest));
    }

    @Test
    @Order(4)
    @DisplayName("Login - Negative")
    public void loginIncorrectPassword() {
        UserService service = new UserService(authDAO, userDAO);

        Assertions.assertDoesNotThrow(() -> service.register(new RegisterRequest(
                "name", "password", "email")));

        LoginRequest loginRequest = new LoginRequest("name", "wrongPassword");
        Assertions.assertThrows(InvalidCredentialsException.class, () -> service.login(loginRequest));
    }

    @Test
    @Order(5)
    @DisplayName("Logout - Positive")
    public void logoutSuccess() {
        UserService service = new UserService(authDAO, userDAO);

        RegisterRequest request = new RegisterRequest("name", "password", "email");
        RegisterResult result = null;
        try {
            result = service.register(request);
        } catch (Exception s) {
            Assertions.fail();
        }

        LogoutRequest logoutRequest = new LogoutRequest(result.authToken());
        Assertions.assertDoesNotThrow(() -> service.logout(logoutRequest));
    }

    @Test
    @Order(6)
    @DisplayName("Logout - Negative")
    public void logoutMissingAuthentication() {
        UserService service = new UserService(authDAO, userDAO);

        LogoutRequest logoutRequest = new LogoutRequest("");
        Assertions.assertThrows(InvalidAuthenticationException.class, () -> service.logout(logoutRequest));
    }

    @Test
    @Order(7)
    @DisplayName("Create Game - Positive")
    public void createGameSuccess() {
        UserService userService = new UserService(authDAO, userDAO);
        GameService gameService = new GameService(authDAO, gameDAO);

        RegisterRequest request = new RegisterRequest("name", "password", "email");
        RegisterResult result = null;
        try {
            result = userService.register(request);
        } catch (Exception s) {
            Assertions.fail();
        }

        CreateGameRequest createGameRequest = new CreateGameRequest(result.authToken(), "gameName");
        Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequest));
    }

    @Test
    @Order(8)
    @DisplayName("Create Game - Negative")
    public void createGameAlreadyTaken() {
        UserService userService = new UserService(authDAO, userDAO);
        GameService gameService = new GameService(authDAO, gameDAO);

        RegisterRequest request = new RegisterRequest("name", "password", "email");
        RegisterResult result = null;
        try {
            result = userService.register(request);
        } catch (Exception s) {
            Assertions.fail();
        }

        CreateGameRequest createGameRequest = new CreateGameRequest(result.authToken(), "gameName");
        Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequest));
        Assertions.assertThrows(BadRequestException.class, () -> gameService.createGame(createGameRequest));
    }

    @Test
    @Order(9)
    @DisplayName("List Games - Positive")
    public void listGamesSuccess() {
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
    public void listGamesFailure() {
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
    public void joinGameSuccess() {
        UserService userService = new UserService(authDAO, userDAO);
        GameService gameService = new GameService(authDAO, gameDAO);

        RegisterRequest request = new RegisterRequest("name", "password", "email");
        RegisterResult result = null;
        try {
            result = userService.register(request);
        } catch (Exception s) {
            Assertions.fail();
        }

        CreateGameRequest createGameRequest = new CreateGameRequest(result.authToken(), "gameName");
        Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequest));

        JoinGameRequest joinRequest = new JoinGameRequest(result.authToken(), "WHITE", 1);
        Assertions.assertDoesNotThrow(() -> gameService.joinGame(joinRequest));
    }

    @Test
    @Order(12)
    @DisplayName("Join Game - Negative")
    public void joinGameColorTaken() {
        UserService userService = new UserService(authDAO, userDAO);
        GameService gameService = new GameService(authDAO, gameDAO);

        RegisterRequest request = new RegisterRequest("name", "password", "email");
        RegisterResult result = null;
        try {
            result = userService.register(request);
        } catch (Exception e) {
            Assertions.fail();
        }

        CreateGameRequest createGameRequest = new CreateGameRequest(result.authToken(), "gameName");
        Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequest));

        JoinGameRequest joinRequest = new JoinGameRequest(result.authToken(), "WHITE", 1);
        Assertions.assertDoesNotThrow(() -> gameService.joinGame(joinRequest));

        JoinGameRequest badRequest = new JoinGameRequest(result.authToken(), "WHITE", 1);
        Assertions.assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(badRequest));
    }

    @Test
    @Order(13)
    @DisplayName("Clear - Positive")
    public void clearServiceSuccess() {
        ClearService service = new ClearService(authDAO, gameDAO, userDAO);

        try {
            service.clear();
        } catch (DataAccessException e) {
            Assertions.fail();
        }

        Assertions.assertTrue(gameDAO.listGames().isEmpty());
    }

    public ListGamesResult setupListTest() throws ServiceException {
        UserService userService = new UserService(authDAO, userDAO);
        GameService gameService = new GameService(authDAO, gameDAO);

        RegisterRequest request = new RegisterRequest("name", "password", "email");

        RegisterResult result = userService.register(request);

        CreateGameRequest createGameRequest = new CreateGameRequest(result.authToken(), "gameName");
        Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequest));

        ListGamesRequest listRequest = new ListGamesRequest(result.authToken());
        return gameService.listGames(listRequest);
    }
}
