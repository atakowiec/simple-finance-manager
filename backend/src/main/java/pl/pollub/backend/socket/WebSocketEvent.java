package pl.pollub.backend.socket;

import lombok.Value;
import org.springframework.web.socket.WebSocketSession;

@Value
public class WebSocketEvent {
    WebSocketSession session;
    String event;
    String message;
}
