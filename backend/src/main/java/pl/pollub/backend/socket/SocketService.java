package pl.pollub.backend.socket;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;


@Component
public class SocketService extends EventWebSocketHandler {
    private final ConcurrentMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private final Map<String, List<Consumer<WebSocketEvent>>> eventListeners = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        if(session.getPrincipal() == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        sessions.put(session.getId(), session);

        System.out.println("Socket connected: " + session.getPrincipal().getName());

        this.handleEvent(session, "connect", null);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        sessions.remove(session.getId());

        System.out.println("Session closed: " + Objects.requireNonNull(session.getPrincipal()).getName());

        this.handleEvent(session, "disconnect", null);
    }

    public void emit(WebSocketSession session, String event, String message) {
        try {
            session.sendMessage(new TextMessage(event + ":" + message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addEventListener(String event, Consumer<WebSocketEvent> listener) {
        eventListeners.computeIfAbsent(event, k -> new ArrayList<>()).add(listener);
    }

    @Override
    protected void handleEvent(WebSocketSession session, String event, String message) {
        if (session.getPrincipal() == null) {
            return;
        }

        List<Consumer<WebSocketEvent>> consumers = eventListeners.get(event);
        if (consumers == null) {
            return;
        }

        for (Consumer<WebSocketEvent> consumer : consumers) {
            consumer.accept(new WebSocketEvent(session, event, message));
        }
    }
}