package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData getUser(String username);

    UserData createUser(String username, String password, String email);

    void clear();
}
