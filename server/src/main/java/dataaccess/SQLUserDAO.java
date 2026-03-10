package dataaccess;

import model.UserData;

public class SQLUserDAO implements UserDAO{

    public UserData getUser(String username) {
        return new UserData("user", "password", "email");
    }

    public UserData createUser(String username, String password, String email) {
        return new UserData("user", "password", "email");
    }

    public void clear() {
        // Implement clear
    }
}
