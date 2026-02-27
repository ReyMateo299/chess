package server;

import dataaccess.DataAccessException;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;

import service.requests.*;
import service.results.*;

import service.ClearService;
import service.UserService;
import dataaccess.*;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    private final ClearService clearService;
    private final UserService userService;

    private String dataAccessType = "Memory";

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.delete("/db", this::clear);
        javalin.exception(DataAccessException.class, this::exceptionHandler);

        AuthDAO authDAO;
        GameDAO gameDAO;
        UserDAO userDAO;

        if (dataAccessType.equals("Memory")) {
            authDAO = new MemoryAuthDAO();
            gameDAO = new MemoryGameDAO();
            userDAO = new MemoryUserDAO();
        }
        else {
            authDAO = null;
            gameDAO = null;
            userDAO = null;
        }

        clearService = new ClearService(authDAO, gameDAO, userDAO);
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
    private void exceptionHandler(DataAccessException ex, Context ctx) {
        ctx.status(400);
        switch (ex.getMessage()) {
            case "Error: bad request" -> ctx.status(400);
            case "Error: invalid login credentials", "Error: unauthorized" -> ctx.status(401);
            case "Error: username already taken" -> ctx.status(403);
        }
        ctx.result(new Gson().toJson(Map.of("message", ex.getMessage())));
    }

    private void register(Context ctx) throws DataAccessException{
        RegisterRequest registerRequest = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult registerResult = userService.register(registerRequest);
        ctx.result(new Gson().toJson(registerResult));
    }

    private void login(Context ctx) throws DataAccessException {
        LoginRequest loginRequest = new Gson().fromJson(ctx.body(), LoginRequest.class);
        LoginResult loginResult = userService.login(loginRequest);
        ctx.result(new Gson().toJson(loginResult));
    }

    private void logout(Context ctx) throws DataAccessException {
        LogoutRequest logoutRequest = new LogoutRequest(ctx.header("authorization"));
        userService.logout(logoutRequest);
    }

    private void clear(Context ctx) {
        clearService.clear();
    }
}
