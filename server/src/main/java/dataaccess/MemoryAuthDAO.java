package dataaccess;

import model.AuthData;

import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO{
    final private HashSet<AuthData> auths = new HashSet<>();

    public void clear() {
        auths.clear();
    }
}
