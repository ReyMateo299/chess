package websocket.messages;

public class LoadGameMessage extends ServerMessage {

    private String game;

    public LoadGameMessage(ServerMessageType type, String game) {
        super(type);
        this.game = game;
    }

    String getGame() {
        return this.game;
    }
}
