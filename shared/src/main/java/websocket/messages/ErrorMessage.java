package websocket.messages;

public class ErrorMessage extends ServerMessage {
    String errorMessage;

    ErrorMessage(ServerMessageType type, String errorMessage) {
        super(type);
        this.errorMessage = errorMessage;
    }

    String getErrorMessage() {
        return this.errorMessage;
    }
}
