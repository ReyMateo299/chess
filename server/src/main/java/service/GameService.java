package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;
import service.exceptions.*;

import service.requests.ListGamesRequest;
import service.results.GameResult;
import service.results.ListGamesResult;

import java.util.ArrayList;

public class GameService {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws ServiceException {
        if (listGamesRequest.authToken().isEmpty()) {
            throw new InvalidAuthenticationException("Error: invalid authentication");
        }
        if (authDAO.getAuth(listGamesRequest.authToken()) == null) {
            throw new InvalidAuthenticationException("Error: invalid authentication");
        }

        ArrayList<GameResult> games = new ArrayList<>();
        for (GameData game : gameDAO.listGames()) {
            games.add(new GameResult(
                    game.gameID(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName()
            ));
        }
        return new ListGamesResult(games);
    }

//    public LoginResult login(LoginRequest loginRequest) {}
//    public void logout(LogoutRequest logoutRequest) {}

}
