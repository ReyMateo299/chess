package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, AuthData> auths = new HashMap<>();

    public AuthData createAuth(String username) {
        String authToken = generateToken();
        AuthData newAuth = new AuthData(authToken, username);
        auths.put(authToken, newAuth);
        return newAuth;
    }

    public boolean deleteAuth(String authToken) {
        if (!auths.containsKey(authToken)) {
            return false;
        }
        auths.remove(authToken);
        return true;
    }

    public void clear() {
        auths.clear();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
