package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;

import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        System.out.println("Message was sent");
        // This is where the server switches on the command type and then runs the necessary code
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> enterGame(command.getAuthToken(), command.getGameID(), ctx.session);
                case LEAVE -> leaveGame(command.getAuthToken(), command.getGameID(), ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void enterGame(String authToken, Integer gameID, Session session) throws IOException {
        connections.add(gameID, session);
        var loadGameMessage = new LoadGameMessage("PUT_GAME_HERE");
        session.getRemote().sendString(new Gson().toJson(loadGameMessage));

        var message = String.format("%s joined game %d", "USER", gameID);
        var notification = new NotificationMessage(message);
        String serializedNotification = new Gson().toJson(notification);
        connections.broadcast(gameID, session, serializedNotification);
    }

    private void leaveGame(String authToken, Integer gameID, Session session) throws IOException {
        connections.remove(gameID, session);
        var message = String.format("%s left the game", "USER");
        var notification = new NotificationMessage(message);
        String serializedNotification = new Gson().toJson(notification);
        connections.broadcast(gameID, session, serializedNotification);
    }


}
