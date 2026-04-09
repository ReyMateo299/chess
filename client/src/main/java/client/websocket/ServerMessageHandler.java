package client.websocket;

import chess.ChessGame;
import websocket.messages.*;

public interface ServerMessageHandler {
    void printLoadGame(ChessGame game, ChessGame.TeamColor teamColor);

    void printErrorMessage(ErrorMessage message);

    void notify(NotificationMessage notification);
}
