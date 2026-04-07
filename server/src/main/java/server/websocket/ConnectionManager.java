package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ConnectionManager {
    public final HashMap<Integer, List<Session>> connections = new HashMap<>();

    public void add(Integer gameID, Session session) {
//        connections.put(session, session);
    }

    public void remove(Integer gameID, Session session) {
//        connections.remove(session);
    }

    public void broadcast(Session excludeSession, ServerMessage message) throws IOException {

    }
}
