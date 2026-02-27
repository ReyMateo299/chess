package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData createAuth(String username);

    boolean deleteAuth(String authToken);

    void clear();
}
