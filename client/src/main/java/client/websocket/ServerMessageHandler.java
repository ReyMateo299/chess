package client.websocket;

import websocket.messages.*;

public interface ServerMessageHandler {
    void printLoadGame(LoadGameMessage message);

    void printErrorMessage(ErrorMessage message);

    void notify(NotificationMessage notification);
}
