package pl.pollub.frontend.event;

import lombok.Value;

@Value
public class EventEmitRequest {
    EventType eventType;
    Object[] data;
}
