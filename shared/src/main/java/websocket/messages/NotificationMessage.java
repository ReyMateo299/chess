package websocket.messages;

public class NotificationMessage extends ServerMessage {
    String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    String getMessage() {
        return this.message;
    }
}
