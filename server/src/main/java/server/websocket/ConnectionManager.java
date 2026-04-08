package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConnectionManager {
    public final HashMap<Integer, List<Session>> connections = new HashMap<>();

    public void add(Integer gameID, Session session) {
        if (!connections.containsKey(gameID)) {
            connections.put(gameID, new ArrayList<>());
        }
        connections.get(gameID).add(session);
    }

//    public List<Session> getSessions(Integer gameID) {
//        if (!connections.containsKey(gameID)) {
//            return null;
//        }
//        return connections.get(gameID);
//    }

    public void remove(Integer gameID, Session session) {
        if (!connections.containsKey(gameID) && connections.get(gameID).contains(session)) {
            connections.remove(gameID);
        }
    }

    public void broadcast(Integer gameID, Session excludeSession, ServerMessage serverMessage) throws IOException {
        String message = serverMessage.toString();
        for (Session c : connections.get(gameID)) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(message);
                }
            }
        }
    }
}
