package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ConnectionManager {
    public final HashMap<Integer, Set<Session>> connections = new HashMap<>();

    public void add(Integer gameID, Session session) {
        if (!connections.containsKey(gameID)) {
            connections.put(gameID, new HashSet<>());
        }
        connections.get(gameID).add(session);
    }

    public void remove(Integer gameID, Session session) {
        if (connections.containsKey(gameID)) {
            connections.get(gameID).remove(session);
        }
    }

    public void broadcast(Integer gameID, Session excludeSession, String message) throws IOException {
        for (Session c : connections.get(gameID)) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(message);
                }
            }
        }
    }
}
