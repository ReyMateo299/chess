package websocket.messages;

import chess.ChessGame.TeamColor;

public class LoadGameMessage extends ServerMessage {

    private String game;
    private TeamColor teamColor;

    public LoadGameMessage(String game, TeamColor teamColor) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.teamColor = teamColor;
    }

    public String getGame() {
        return this.game;
    }

    public TeamColor getTeamColor() {
        return this.teamColor;
    }
}
