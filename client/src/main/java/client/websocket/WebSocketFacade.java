package client.websocket;

import chess.ChessGame;
import client.ResponseException;

import com.google.gson.Gson;

import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class WebSocketFacade extends Endpoint {

    // Do the freaking notification thing
    // Notification just a stupid pointer to the gameplay ui cuz bruh..
    // This facade aint the place to print messages to the dang console

    Session session;
    ServerMessageHandler serverMessageHandler;

    public WebSocketFacade(String url, ServerMessageHandler serverMessageHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.serverMessageHandler = serverMessageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case ERROR -> handleErrorMessage(new Gson().fromJson(message, ErrorMessage.class));
                        case LOAD_GAME -> handleLoadGameMessage(new Gson().fromJson(message, LoadGameMessage.class));
                        case NOTIFICATION -> handleNotification(new Gson().fromJson(message, NotificationMessage.class));
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    // Do I really need this endpoint? Why is my class extending this?
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void sendCommand(UserGameCommand command) throws ResponseException {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    private void handleErrorMessage(ErrorMessage errorMessage) {
        serverMessageHandler.printErrorMessage(errorMessage);
    }

    private void handleLoadGameMessage(LoadGameMessage loadGameMessage) {
        ChessGame game = new Gson().fromJson(loadGameMessage.getGame(), ChessGame.class);
        serverMessageHandler.printLoadGame(game);
    }

    private void handleNotification(NotificationMessage notification) {
        serverMessageHandler.notify(notification);
    }
}
