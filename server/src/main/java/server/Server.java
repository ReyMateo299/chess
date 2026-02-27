package server;

import dataaccess.DataAccessException;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;

import service.requests.*;
import service.results.*;
import service.exceptions.*;

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
        javalin.exception(ServiceException.class, this::exceptionHandler);

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
    private void exceptionHandler(ServiceException ex, Context ctx) {
        ctx.status(400);
        switch (ex) {
            case BadRequestException bre -> ctx.status(400);
            case InvalidCredentialsException ice -> ctx.status(401);
            case InvalidAuthenticationException iae -> ctx.status(401);
            case AlreadyTakenException ate -> ctx.status(403);
            default -> ctx.status(500);
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

    private void clear(Context ctx) {
        clearService.clear();
    }
}
