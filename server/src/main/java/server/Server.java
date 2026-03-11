package server;

import dataaccess.DataAccessException;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;

import service.requests.*;
import service.results.*;
import service.exceptions.*;

import service.ClearService;
import service.GameService;
import service.UserService;

import dataaccess.*;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    private final ClearService clearService;
    private final GameService gameService;
    private final UserService userService;

    private final String dataAccessType = "SQL";
//    private final String dataAccessType = "MEMORY";

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);
        javalin.delete("/db", this::clear);
        javalin.exception(ServiceException.class, this::exceptionHandler);

        AuthDAO authDAO;
        GameDAO gameDAO;
        UserDAO userDAO;

        if (dataAccessType.equals("MEMORY")) {
            authDAO = new MemoryAuthDAO();
            gameDAO = new MemoryGameDAO();
            userDAO = new MemoryUserDAO();
        }
        else {
            try {
                authDAO = new SQLAuthDAO();
                gameDAO = new MemoryGameDAO();
                userDAO = new SQLUserDAO();
            } catch (DataAccessException e) {
                authDAO = new MemoryAuthDAO();
                gameDAO = new MemoryGameDAO();
                userDAO = new MemoryUserDAO();
            }
        }

        clearService = new ClearService(authDAO, gameDAO, userDAO);
        gameService = new GameService(authDAO, gameDAO);
        userService = new UserService(authDAO, userDAO);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    // delete this??
    private void exceptionHandler(ServiceException ex, Context ctx) {
        ctx.status(400);
        switch (ex) {
            case BadRequestException bre -> ctx.status(400);
            case GameNotFoundException exc -> ctx.status(400);
            case InvalidCredentialsException ice -> ctx.status(401);
            case InvalidAuthenticationException iae -> ctx.status(401);
            case AlreadyTakenException ate -> ctx.status(403);
            case ServiceException s -> ctx.status(500);
        }
        ctx.result(new Gson().toJson(Map.of("message", ex.getMessage())));
    }

    private void register(Context ctx) throws ServiceException {
        RegisterRequest registerRequest = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult registerResult = userService.register(registerRequest);
        ctx.result(new Gson().toJson(registerResult));
    }

    private void login(Context ctx) throws ServiceException {
        LoginRequest loginRequest = new Gson().fromJson(ctx.body(), LoginRequest.class);
        LoginResult loginResult = userService.login(loginRequest);
        ctx.result(new Gson().toJson(loginResult));
    }

    private void logout(Context ctx) throws ServiceException {
        LogoutRequest logoutRequest = new LogoutRequest(ctx.header("authorization"));
        userService.logout(logoutRequest);
    }

    private void listGames(Context ctx) throws ServiceException, DataAccessException {
        ListGamesRequest listGamesRequest = new ListGamesRequest(ctx.header("authorization"));
        ListGamesResult result = gameService.listGames(listGamesRequest);
        ctx.result(new Gson().toJson(result));
    }

    private void createGame(Context ctx) throws ServiceException, DataAccessException {
        String authToken = ctx.header("authorization");
        GameName gameName = new Gson().fromJson(ctx.body(), GameName.class);
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, gameName.gameName());
        CreateGameResult result = gameService.createGame(createGameRequest);
        ctx.result(new Gson().toJson(result));
    }

    private void joinGame(Context ctx) throws ServiceException, DataAccessException {
        String authToken = ctx.header("authorization");
        JoinData joinData = new Gson().fromJson(ctx.body(), JoinData.class);
        JoinGameRequest request = new JoinGameRequest(authToken, joinData.playerColor(), joinData.gameID());
        gameService.joinGame(request);
    }

    private void clear(Context ctx) throws DataAccessException {
        clearService.clear();
    }
}
