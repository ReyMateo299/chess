package server;

//import exception.ResponseException;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;

import service.ClearService;
import dataaccess.*;

public class Server {

    private final Javalin javalin;

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    private String dataAccessType = "Memory";

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", this::clear);

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
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void clear(Context ctx) {
        ClearService clearService = new ClearService(
                this.authDAO,
                this.gameDAO,
                this.userDAO
        );
        clearService.clear();
    }
}
