package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;

import model.UserData;
import model.AuthData;

import org.junit.jupiter.api.Assertions;
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
        UserData newUser = null;
        AuthData newAuth = null;

        try {
            if (registerRequest.username() == null || registerRequest.password() == null
                    || registerRequest.email() == null) {
                throw new BadRequestException("Error: bad request");
            }
            if (userDAO.getUser(registerRequest.username()) != null) {
                throw new AlreadyTakenException("Error: username already taken");
            }

            // should I just pass in a UserData object here?
            newUser = userDAO.createUser(
                    registerRequest.username(),
                    registerRequest.password(),
                    registerRequest.email()
            );
            newAuth = authDAO.createAuth(registerRequest.username());
        } catch (DataAccessException e) {
            throw new ServiceException("Internal Server Error");
        }

        return new RegisterResult(newUser.username(), newAuth.authToken());
    }

    public LoginResult login(LoginRequest loginRequest) throws ServiceException, DataAccessException {
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

    public void logout(LogoutRequest logoutRequest) throws ServiceException, DataAccessException {
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
