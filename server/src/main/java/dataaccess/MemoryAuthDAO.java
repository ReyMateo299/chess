package dataaccess;

import model.AuthData;

import java.util.HashSet;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    final private HashSet<AuthData> auths = new HashSet<>();

    public AuthData createAuth(String username) {
        AuthData newAuth = new AuthData(generateToken(), username);
        auths.add(newAuth);
        return newAuth;
    }

    public void clear() {
        auths.clear();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
