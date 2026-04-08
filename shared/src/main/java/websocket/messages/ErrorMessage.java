package websocket.messages;

public class ErrorMessage extends ServerMessage {
    String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

    String getErrorMessage() {
        return this.errorMessage;
    }
}
