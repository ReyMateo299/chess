package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData createAuth(String username) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    boolean deleteAuth(String authToken) throws DataAccessException;

    void clear() throws DataAccessException;
}
