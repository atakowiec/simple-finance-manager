package pl.pollub.frontend.event;

import lombok.Value;

import java.lang.reflect.Method;

@Value
public class EventListener {
    EventType eventType;
    Object listener;
    Method method;
}
