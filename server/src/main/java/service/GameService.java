package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;
import service.exceptions.*;

import service.requests.CreateGameRequest;
import service.requests.ListGamesRequest;
import service.results.CreateGameResult;
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

    public CreateGameResult createGame(CreateGameRequest request) throws ServiceException {
        checkAuthorization(request.authToken());
        if (request.gameName() == null) {
            throw new BadRequestException("Error: invalid game name");
        }
        if (gameDAO.getGame(request.gameName()) != null) {
            throw new BadRequestException("Error: game name already in use");
        }
        GameData newGame = gameDAO.createGame(request.gameName());
        return new CreateGameResult(newGame.gameID());
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws ServiceException {
        checkAuthorization(listGamesRequest.authToken());
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


//    private AuthData checkAuthorization(String authToken) throws InvalidAuthenticationException {
//        if (authToken.isEmpty()) {
//            throw new InvalidAuthenticationException("Error: invalid authentication");
//        }
//        AuthData auth = authDAO.getAuth(authToken);
//        if (auth == null) {
//            throw new InvalidAuthenticationException("Error: invalid authentication");
//        }
//        return auth;
//    }

    private void checkAuthorization(String authToken) throws InvalidAuthenticationException {
        if (authToken.isEmpty() || authDAO.getAuth(authToken) == null) {
            throw new InvalidAuthenticationException("Error: invalid authentication");
        }
    }

}
