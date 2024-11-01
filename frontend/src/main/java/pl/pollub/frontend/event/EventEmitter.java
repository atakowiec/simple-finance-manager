package pl.pollub.frontend.event;

import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.injector.DependencyInjector;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.injector.Injectable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Injectable
public class EventEmitter {
    @Inject
    private DependencyInjector dependencyInjector;

    private final Map<EventType, List<EventListener>> listeners = new HashMap<>();

    private final Set<Object> activeControllers = new HashSet<>();

    @PostInitialize
    private void scanForListeners() {
        for (Object instance : dependencyInjector.getInstances()) {
            for (Method method : instance.getClass().getDeclaredMethods()) {
                if (!method.isAnnotationPresent(OnEvent.class)) continue;

                method.setAccessible(true);


                EventType eventType = method.getAnnotation(OnEvent.class).value();
                EventListener eventListener = new EventListener(eventType, instance, method);

                listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(eventListener);
            }
        }
    }

    @OnEvent(EventType.VIEW_CHANGE)
    private void onViewChange(Object... data) {
        Object controller = data[0];

        activeControllers.clear();
        registerController(controller);
    }

    public void registerController(Object controller) {
        activeControllers.add(controller);
    }

    public void unregisterController(Object controller) {
        activeControllers.remove(controller);
    }

    public void emit(EventType eventType, Object... data) {
        emitToControllers(eventType, data);
        emitToListeners(eventType, data);
    }

    private void emitToListeners(EventType eventType, Object... data) {
        List<EventListener> eventListeners = listeners.get(eventType);
        if (eventListeners == null) return;

        for (EventListener eventListener : eventListeners) {
            invokeListenerMethod(eventListener.getMethod(), eventListener.getListener(), data);
        }
    }

    private void emitToControllers(EventType eventType, Object... data) {
        for (Object controller : activeControllers) {
            for (Method method : controller.getClass().getDeclaredMethods()) {
                if (!method.isAnnotationPresent(OnEvent.class))
                    continue;
                if (!method.getAnnotation(OnEvent.class).value().equals(eventType))
                    continue;

                method.setAccessible(true);
                invokeListenerMethod(method, controller, data);
            }
        }
    }

    private void invokeListenerMethod(Method method, Object target, Object[] data) {
        try {
            if(method.getParameterCount() == 0) {
                method.invoke(target);
            } else if (method.getParameterCount() == 1) {
                method.invoke(target, (Object) data);
            } else {
                method.invoke(target, data);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
