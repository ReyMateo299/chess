package client.websocket;

import client.ResponseException;

import com.google.gson.Gson;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    // Do the freaking notification thing
    // Notification just a stupid pointer to the gameplay ui cuz bruh..
    // This facade aint the place to print messages to the dang console

    Session session;

    public WebSocketFacade(String url) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    System.out.println("Message sent: " + message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    // Do I really need this endpoint? Why is my class extending this?
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    // Add a method for each command
}
