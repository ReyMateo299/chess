package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;

import dataaccess.DataAccessException;

import model.UserData;
import model.AuthData;

import service.requests.RegisterRequest;
import service.results.RegisterResult;

public class UserService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
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

//    public LoginResult login(LoginRequest loginRequest) {}
//    public void logout(LogoutRequest logoutRequest) {}
}
