package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;

import dataaccess.DataAccessException;

import model.UserData;
import model.AuthData;

import service.requests.*;
import service.results.*;

public class UserService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        if (registerRequest.username() == null || registerRequest.password() == null
            || registerRequest.email() == null) {
            throw new DataAccessException("Error: bad request");
        }
        if (userDAO.getUser(registerRequest.username()) != null) {
            throw new DataAccessException("Error: username already taken");
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

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new DataAccessException("Error: bad request");
        }
        UserData user = userDAO.getUser(loginRequest.username());
        if (user == null) {
            throw new DataAccessException("Error: invalid login credentials");
        }
        if (!isValidPassword(user, loginRequest.password())) {
            throw new DataAccessException("Error: invalid login credentials");
        }

        AuthData newAuth = authDAO.createAuth(loginRequest.username());
        return new LoginResult(user.username(), newAuth.authToken());
    }

    private boolean isValidPassword(UserData user, String password) {
        return user.password().equals(password);
    }

//    public void logout(LogoutRequest logoutRequest) {}
}
