package ui;

import client.State;

public record UIResult(String message, State nextState, String authToken) {
}
