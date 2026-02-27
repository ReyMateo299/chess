package service.exceptions;

public class InvalidAuthenticationException extends ServiceException {
    public InvalidAuthenticationException(String message) {
        super(message);
    }
}
