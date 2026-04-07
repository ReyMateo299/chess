package ui;

import client.OpenWebsocket;
import client.State;
import client.websocket.WebSocketFacade;

public record UIResult(String message, State nextState, String authToken, OpenWebsocket openWebsocket) {
}
