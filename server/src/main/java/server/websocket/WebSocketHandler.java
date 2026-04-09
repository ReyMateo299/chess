package server.websocket;

import chess.ChessGame;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.*;
import org.eclipse.jetty.websocket.api.Session;

import service.exceptions.ServiceException;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.util.Collection;

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
                case MAKE_MOVE -> makeMove(new Gson().fromJson(ctx.message(), MakeMoveCommand.class), ctx.session);
                case RESIGN -> resign(command.getAuthToken(), command.getGameID(), ctx.session);
                case LEAVE -> leaveGame(command.getAuthToken(), command.getGameID(), ctx.session);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void enterGame(String authToken, Integer gameID, Session session) throws IOException, ServiceException {

        AuthData user = getUser(authToken);
        GameData game = getGame(gameID);

        if (!userExists(user, session)) {
            return;
        }
        if (game == null) {
            var errorMessage = new ErrorMessage("ERROR: game doesn't exist");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        String username = user.username();
        TeamColor color = null;

        if (game.whiteUsername().equals(username)) {
            color = TeamColor.WHITE;
        } else if (game.blackUsername().equals(username)) {
            color = TeamColor.BLACK;
        }

        connections.add(gameID, session);
        var loadGameMessage = new LoadGameMessage(new Gson().toJson(game.game()), color);
        session.getRemote().sendString(new Gson().toJson(loadGameMessage));

        String message;
        if (color != null) {
            message = String.format("%s connected as team %s", username, color);
        } else {
            message = String.format("%s connected as an observer", username);
        }

        var notification = new NotificationMessage(message);
        String serializedNotification = new Gson().toJson(notification);
        connections.broadcast(gameID, session, serializedNotification);
    }

    private void makeMove(MakeMoveCommand command, Session session) throws IOException, ServiceException {
        Integer gameID = command.getGameID();
        AuthData user = getUser(command.getAuthToken());
        GameData gameData = getGame(gameID);

        if (!userExists(user, session)) {
            return;
        }
        if (gameData == null) {
            var errorMessage = new ErrorMessage("ERROR: game doesn't exist");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        String username = user.username();
        TeamColor playerColor = null;

        if (gameData.whiteUsername().equals(username)) {
            playerColor = TeamColor.WHITE;
        } else if (gameData.blackUsername().equals(username)) {
            playerColor = TeamColor.BLACK;
        }

        ChessGame game = gameData.game();
        TeamColor teamTurn = game.getTeamTurn();

        if (playerColor != teamTurn) {
            var errorMessage = new ErrorMessage("ERROR: can't make a move now");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        ChessMove move = command.getMove();
        ChessPosition startPosition = move.getStartPosition();
//        ChessPosition endPosition = move.getEndPosition();
        Collection<ChessMove> validMoves = game.validMoves(startPosition);

        if (!validMoves.contains(move)) {
            var errorMessage = new ErrorMessage("ERROR: invalid move");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        //TODO: Add fuctionality to update the chess game

        // Send Load_game message
        var loadGameMessage = new LoadGameMessage(new Gson().toJson(game), playerColor);
        String serializedMessage = new Gson().toJson(loadGameMessage);
        connections.broadcast(gameID, null, serializedMessage);

        // Send notification of the move to all other clients
        String message = "Player made move: INSERT_MOVE_HERE";
        var notification = new NotificationMessage(message);
        String serializedNotification = new Gson().toJson(notification);
        connections.broadcast(gameID, session, serializedNotification);

        // Send move result notification to all clients
        if (game.isInCheck(TeamColor.WHITE)) {
            message = "WHITE is in check";
            notification = new NotificationMessage(message);
            serializedNotification = new Gson().toJson(notification);
            connections.broadcast(gameID, null, serializedNotification);
        }
        if (game.isInCheck(TeamColor.BLACK)) {
            message = "BLACK is in check";
            notification = new NotificationMessage(message);
            serializedNotification = new Gson().toJson(notification);
            connections.broadcast(gameID, null, serializedNotification);
        }
        if (game.isInStalemate(TeamColor.WHITE) || game.isInStalemate(TeamColor.BLACK)) {
            message = "Stalemate!";
            notification = new NotificationMessage(message);
            serializedNotification = new Gson().toJson(notification);
            connections.broadcast(gameID, null, serializedNotification);
        }
        if (game.isInCheckmate(TeamColor.WHITE) || game.isInStalemate(TeamColor.BLACK)) {
            message = "Checkmate!";
            notification = new NotificationMessage(message);
            serializedNotification = new Gson().toJson(notification);
            connections.broadcast(gameID, null, serializedNotification);
        }
    }

    private void resign(String authToken, Integer gameID, Session session) throws ServiceException, IOException {
        // TODO: Have the server mark the game as over
        AuthData user = getUser(authToken);

        if (!userExists(user, session)) {
            return;
        }

        String message = String.format("%s has resigned. The game has ended.", user.username());
        var notification = new NotificationMessage(message);
        String serializedNotification = new Gson().toJson(notification);
        connections.broadcast(gameID, null, serializedNotification);
    }

    private void leaveGame(String authToken, Integer gameID, Session session) throws ServiceException, IOException {
        AuthData user = getUser(authToken);
        GameData gameData = getGame(gameID);

        if (!userExists(user, session)) {
            return;
        }
        if (gameData == null) {
            var errorMessage = new ErrorMessage("ERROR: game doesn't exist");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        String username = user.username();
        TeamColor playerColor = null;

        if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(username)) {
            playerColor = TeamColor.WHITE;
        } else if (gameData.blackUsername() != null && gameData.blackUsername().equals(username)) {
            playerColor = TeamColor.BLACK;
        }

        // Remove play from game in database
        String color;
        if (playerColor == TeamColor.WHITE) {
            color = "WHITE";
        } else {
            color = "BLACK";
        }
        if (playerColor != null) {
            try {
                gameDAO.removePlayer(gameID, color);
            } catch (DataAccessException e) {
                throw new ServiceException(e.getMessage());
            }
        }

        connections.remove(gameID, session);
        var message = String.format("%s left the game", user.username());
        var notification = new NotificationMessage(message);
        String serializedNotification = new Gson().toJson(notification);
        connections.broadcast(gameID, null, serializedNotification);
    }

    private AuthData getUser(String authToken) throws ServiceException {
        AuthData user;
        try {
            return authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new ServiceException("Internal Server Error");
        }
    }

    private GameData getGame(Integer gameID) throws ServiceException {
        GameData game;
        try {
            return gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            throw new ServiceException("Internal Server Error");
        }
    }

    private Boolean userExists(AuthData user, Session session) throws IOException{
        if (user == null) {
            var errorMessage = new ErrorMessage("ERROR: bad authorization");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return false;
        }
        return true;
    }

}
