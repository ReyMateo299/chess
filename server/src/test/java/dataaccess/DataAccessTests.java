package dataaccess;

import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import service.exceptions.*;
import service.requests.*;
import service.results.GameResult;
import service.results.ListGamesResult;
import service.results.RegisterResult;

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

//    @Test
//    @Order(6)
//    @DisplayName("Logout - Negative")
//    public void logoutMissingAuthentication() {
//        UserService service = new UserService(authDAO, userDAO);
//
//        LogoutRequest logoutRequest = new LogoutRequest("");
//        Assertions.assertThrows(InvalidAuthenticationException.class, () -> service.logout(logoutRequest));
//    }
//
//    @Test
//    @Order(7)
//    @DisplayName("Create Game - Positive")
//    public void createGameSuccess() {
//        UserService userService = new UserService(authDAO, userDAO);
//        GameService gameService = new GameService(authDAO, gameDAO);
//
//        RegisterRequest request = new RegisterRequest("name", "password", "email");
//        RegisterResult result = null;
//        try {
//            result = userService.register(request);
//        } catch (Exception s) {
//            Assertions.fail();
//        }
//
//        CreateGameRequest createGameRequest = new CreateGameRequest(result.authToken(), "gameName");
//        Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequest));
//    }
//
//    @Test
//    @Order(8)
//    @DisplayName("Create Game - Negative")
//    public void createGameAlreadyTaken() {
//        UserService userService = new UserService(authDAO, userDAO);
//        GameService gameService = new GameService(authDAO, gameDAO);
//
//        RegisterRequest request = new RegisterRequest("name", "password", "email");
//        RegisterResult result = null;
//        try {
//            result = userService.register(request);
//        } catch (Exception s) {
//            Assertions.fail();
//        }
//
//        CreateGameRequest createGameRequest = new CreateGameRequest(result.authToken(), "gameName");
//        Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequest));
//        Assertions.assertThrows(BadRequestException.class, () -> gameService.createGame(createGameRequest));
//    }
//
//    @Test
//    @Order(9)
//    @DisplayName("List Games - Positive")
//    public void listGamesSuccess() {
//        ListGamesResult listResult = null;
//        try {
//            listResult = setupListTest();
//        } catch (ServiceException s) {
//            Assertions.fail();
//        }
//
//        ListGamesResult expected = new ListGamesResult(List.of(new GameResult(
//                1, null, null, "gameName"
//        )));
//        Assertions.assertEquals(listResult.games(), expected.games());
//    }
//
//    @Test
//    @Order(10)
//    @DisplayName("List Games - Negative (Empty fields are null not strings")
//    public void listGamesFailure() {
//        ListGamesResult listResult = null;
//        try {
//            listResult = setupListTest();
//        } catch (ServiceException s) {
//            Assertions.fail();
//        }
//
//        ListGamesResult expectedFalse = new ListGamesResult(List.of(new GameResult(
//                1, "", "", "gameName"
//        )));
//        Assertions.assertNotEquals(listResult.games(), expectedFalse.games());
//    }
//
//    @Test
//    @Order(11)
//    @DisplayName("Join Game - Positive")
//    public void joinGameSuccess() {
//        UserService userService = new UserService(authDAO, userDAO);
//        GameService gameService = new GameService(authDAO, gameDAO);
//
//        RegisterRequest request = new RegisterRequest("name", "password", "email");
//        RegisterResult result = null;
//        try {
//            result = userService.register(request);
//        } catch (Exception s) {
//            Assertions.fail();
//        }
//
//        CreateGameRequest createGameRequest = new CreateGameRequest(result.authToken(), "gameName");
//        Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequest));
//
//        JoinGameRequest joinRequest = new JoinGameRequest(result.authToken(), "WHITE", 1);
//        Assertions.assertDoesNotThrow(() -> gameService.joinGame(joinRequest));
//    }
//
//    @Test
//    @Order(12)
//    @DisplayName("Join Game - Negative")
//    public void joinGameColorTaken() {
//        UserService userService = new UserService(authDAO, userDAO);
//        GameService gameService = new GameService(authDAO, gameDAO);
//
//        RegisterRequest request = new RegisterRequest("name", "password", "email");
//        RegisterResult result = null;
//        try {
//            result = userService.register(request);
//        } catch (Exception e) {
//            Assertions.fail();
//        }
//
//        CreateGameRequest createGameRequest = new CreateGameRequest(result.authToken(), "gameName");
//        Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequest));
//
//        JoinGameRequest joinRequest = new JoinGameRequest(result.authToken(), "WHITE", 1);
//        Assertions.assertDoesNotThrow(() -> gameService.joinGame(joinRequest));
//
//        JoinGameRequest badRequest = new JoinGameRequest(result.authToken(), "WHITE", 1);
//        Assertions.assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(badRequest));
//    }
//
//    @Test
//    @Order(13)
//    @DisplayName("Clear - Positive")
//    public void clearServiceSuccess() throws DataAccessException {
//        ClearService service = new ClearService(authDAO, gameDAO, userDAO);
//
//        try {
//            service.clear();
//        } catch (Exception e) {
//            Assertions.fail();
//        }
//
//        Assertions.assertTrue(gameDAO.listGames().isEmpty());
//    }
//
//    public ListGamesResult setupListTest() throws ServiceException {
//        UserService userService = new UserService(authDAO, userDAO);
//        GameService gameService = new GameService(authDAO, gameDAO);
//
//        RegisterRequest request = new RegisterRequest("name", "password", "email");
//
//        RegisterResult result = userService.register(request);
//
//        CreateGameRequest createGameRequest = new CreateGameRequest(result.authToken(), "gameName");
//        Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequest));
//
//        ListGamesRequest listRequest = new ListGamesRequest(result.authToken());
//        return gameService.listGames(listRequest);
//    }
}
