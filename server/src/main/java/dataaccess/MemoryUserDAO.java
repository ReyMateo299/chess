package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    final private HashMap<String, UserData> users = new HashMap<>();

    public UserData getUser(String username) {
        if (users.containsKey(username)) {
            return users.get(username);
        }
        return null;
    }

    public UserData createUser(String username, String password, String email) {
        UserData newUser = new UserData(username, password, email);
        users.put(username, newUser);
        return newUser;
    }

    public void clear() {
        users.clear();
    }
}
