package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;

import model.UserData;
import model.AuthData;

import service.exceptions.*;
import service.requests.*;
import service.results.*;

public class UserService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws ServiceException {
        if (registerRequest.username() == null || registerRequest.password() == null
            || registerRequest.email() == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (userDAO.getUser(registerRequest.username()) != null) {
            throw new AlreadyTakenException("Error: username already taken");
        }

        // should I just pass in a UserData object here?
        UserData newUser = userDAO.createUser(
                registerRequest.username(),
                registerRequest.password(),
                registerRequest.email()
        );
        AuthData newAuth = authDAO.createAuth(registerRequest.username());

        return new RegisterResult(newUser.username(), newAuth.authToken());
    }

    public LoginResult login(LoginRequest loginRequest) throws ServiceException {
        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new BadRequestException("Error: missing username and/or password");
        }
        UserData user = userDAO.getUser(loginRequest.username());
        if (user == null) {
            throw new InvalidCredentialsException("Error: username not found");
        }
        if (!isValidPassword(user, loginRequest.password())) {
            throw new InvalidCredentialsException("Error: invalid password");
        }

        AuthData newAuth = authDAO.createAuth(loginRequest.username());
        return new LoginResult(user.username(), newAuth.authToken());
    }

    public void logout(LogoutRequest logoutRequest) throws ServiceException {
        if (logoutRequest.authToken().isEmpty()) {
            throw new InvalidAuthenticationException("Error: invalid authentication");
        }
        boolean deleteSuccess = authDAO.deleteAuth(logoutRequest.authToken());
        if (!deleteSuccess) {
            throw new InvalidAuthenticationException("Error: invalid authentication");
        }
    }

    private boolean isValidPassword(UserData user, String password) {
        return user.password().equals(password);
    }

}
