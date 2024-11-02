package pl.pollub.frontend.service;

import pl.pollub.frontend.event.EventEmitter;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.event.OnEvent;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.injector.Injectable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

@Injectable
public class SocketService implements WebSocket.Listener {
    private WebSocket webSocket;
    @Inject
    private EventEmitter eventEmitter;
    @Inject
    private AuthService authService;

    public void connect() {
        if (!authService.isLoggedIn())
            throw new IllegalStateException("Cannot connect to websocket without being logged in");

        if (this.webSocket != null)
            this.webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "User logged out");

        HttpClient client = HttpClient.newHttpClient();
        webSocket = client.newWebSocketBuilder()
                .header("Authorization", "Bearer " + authService.getToken())
                .buildAsync(URI.create("ws://localhost:5000/ws"), this)
                .join();
    }

    public void disconnect() {
        if (webSocket != null)
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "User logged out");
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        eventEmitter.emit(EventType.WEBSOCKET_CONNECTED);
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        String[] parts = data.toString().split(":", 2);

        eventEmitter.emit(EventType.WEBSOCKET_MESSAGE, parts[0], parts.length > 1 ? parts[1] : null);
        webSocket.request(1);
        return null;
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        eventEmitter.emit(EventType.WEBSOCKET_DISCONNECTED);
        return null;
    }

    public void emit(String event, String message) {
        if (event.contains(":"))
            throw new IllegalArgumentException("Event cannot contain ':' character");

        webSocket.sendText(event + ":" + message, true);
    }

    @OnEvent(EventType.WEBSOCKET_CONNECTED)
    public void onConnected() {
        System.out.println("Connected to websocket");
    }

    @OnEvent(EventType.WEBSOCKET_DISCONNECTED)
    public void onDisconnected() {
        System.out.println("Disconnected from websocket");
    }
}
