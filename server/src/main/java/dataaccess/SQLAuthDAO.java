package dataaccess;

import model.AuthData;

public class SQLAuthDAO implements AuthDAO {

    public AuthData createAuth(String username) {
        return new AuthData("authToken", "username");
    }

    public AuthData getAuth(String authToken) {
        return new AuthData("authToken", "username");
    }

    public boolean deleteAuth(String authToken) {
        return false;
    }

    public void clear() {
        // Implement clear
    }
}
