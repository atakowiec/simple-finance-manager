package pl.pollub.backend.socket;

import lombok.NonNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public abstract class EventWebSocketHandler extends TextWebSocketHandler {
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        String[] parts = payload.split(":", 2);

        String event = parts[0];

        String data = parts.length > 1 ? parts[1] : null;

        handleEvent(session, event, data);
    }

    protected abstract void handleEvent(WebSocketSession session, String event, String message);
}
