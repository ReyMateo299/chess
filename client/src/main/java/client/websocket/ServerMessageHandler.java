package client.websocket;

import chess.ChessGame;
import websocket.messages.*;

public interface ServerMessageHandler {
    void printLoadGame(ChessGame game);

    void printErrorMessage(ErrorMessage message);

    void notify(NotificationMessage notification);
}
