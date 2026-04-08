package websocket.messages;

public class LoadGameMessage extends ServerMessage {

    private String game;

    public LoadGameMessage(String game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    String getGame() {
        return this.game;
    }
}
