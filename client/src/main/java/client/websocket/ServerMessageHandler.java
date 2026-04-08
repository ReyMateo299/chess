package client.websocket;

import websocket.messages.*;

public interface ServerMessageHandler {
    void sendLoadGame(LoadGameMessage message);

    void sendErrorMessage(ErrorMessage message);

    void notify(NotificationMessage notification);
}
