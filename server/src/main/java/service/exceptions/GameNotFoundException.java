package service.exceptions;

public class GameNotFoundException extends ServiceException {
    public GameNotFoundException(String message) {
        super(message);
    }
}
