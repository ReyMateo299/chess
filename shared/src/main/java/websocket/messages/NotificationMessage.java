package websocket.messages;

public class NotificationMessage extends ServerMessage {
    String message;

    NotificationMessage(ServerMessage.ServerMessageType type, String message) {
        super(type);
        this.message = message;
    }

    String getMessage() {
        return this.message;
    }
}
