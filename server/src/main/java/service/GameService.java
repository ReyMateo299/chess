package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import service.exceptions.*;

import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.requests.ListGamesRequest;
import service.results.CreateGameResult;
import service.results.GameResult;
import service.results.ListGamesResult;

import java.util.ArrayList;
import java.util.Set;

public class GameService {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final Set<String> colors;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.colors = Set.of("WHITE", "BLACK");
    }

    public CreateGameResult createGame(CreateGameRequest request) throws ServiceException {
        try {
            checkAuthorization(request.authToken());
            if (request.gameName() == null) {
                throw new BadRequestException("Error: invalid game name");
            }
            if (gameDAO.getGame(request.gameName()) != null) {
                throw new BadRequestException("Error: game name already in use");
            }
            GameData newGame = gameDAO.createGame(request.gameName());
            return new CreateGameResult(newGame.gameID());
        } catch (DataAccessException e) {
            throw new ServiceException("Internal Server Error");
        }
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws ServiceException {
        try {
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
        } catch (DataAccessException e) {
            throw new ServiceException("Internal Server Error");
        }
    }

    public void joinGame(JoinGameRequest request) throws ServiceException {
        try {
            AuthData authData = checkAuthorization(request.authToken());
            if (request.gameID() == null || request.playerColor() == null
                    || !colors.contains(request.playerColor())) {
                throw new BadRequestException("Error: bad request");
            }
            GameData gameData = gameDAO.getGame(request.gameID());
            if (gameData == null) {
                throw new GameNotFoundException("Error: game not found");
            }
            GameData updatedGame = gameDAO.updateGame(request.gameID(), authData.username(), request.playerColor());
            if (updatedGame == null) {
                throw new AlreadyTakenException("Error: team already taken");
            }
        } catch (DataAccessException e) {
            throw new ServiceException("Internal Server Error");
        }
    }

    private AuthData checkAuthorization(String authToken) throws ServiceException {
        try {
            if (authToken == null || authToken.isEmpty()) {
                throw new InvalidAuthenticationException("Error: invalid authentication");
            }
            AuthData auth = authDAO.getAuth(authToken);
            if (auth == null) {
                throw new InvalidAuthenticationException("Error: invalid authentication");
            }
            return auth;
        } catch (DataAccessException e) {
            throw new ServiceException("Internal Server Error");
        }
    }

}
