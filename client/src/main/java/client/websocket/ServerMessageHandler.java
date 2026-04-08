package client.websocket;

import websocket.messages.*;

public interface ServerMessageHandler {
    void notify(NotificationMessage notification);
}
