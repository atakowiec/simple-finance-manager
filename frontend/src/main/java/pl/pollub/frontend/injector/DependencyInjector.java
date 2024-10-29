package pl.pollub.frontend.injector;

import lombok.Getter;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import pl.pollub.frontend.annotation.PostInitialize;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class DependencyInjector {
    @Getter
    private final Map<Class<?>, Object> instances = new HashMap<>();

    private DependencyInjector() {
        instances.put(DependencyInjector.class, this);
        this.begin();
    }

    public static DependencyInjector init() {
        return new DependencyInjector();
    }


    private  <T> T getInstance(Class<T> clazz) {
        return clazz.cast(instances.get(clazz));
    }

    private void begin() {
        Reflections reflections = new Reflections("pl.pollub.frontend", Scanners.TypesAnnotated);

        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Injectable.class);

        for (Class<?> clazz : annotatedClasses) {
            addInstance(clazz);
        }

        for (Object instance : instances.values()) {
            manualInject(instance);
        }

        for (Object instance : instances.values()) {
            runPostInitialize(instance);
        }
    }

    public void addInstance(Class<?> clazz) {
        try {
            instances.put(clazz, clazz.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            throw new RuntimeException("Cannot create instance of class: " + clazz.getName(), e);
        }
    }

    public void manualInject(Object instance) {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if(!field.isAnnotationPresent(Inject.class)) {
                continue;
            }

            Object objectToInject = this.getInstance(field.getType());
            if (objectToInject == null) {
                continue;
            }

            try {
                field.setAccessible(true);
                field.set(instance, objectToInject);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void runPostInitialize(Object controller) {
        for (Method method : controller.getClass().getMethods()) {
            if (!method.isAnnotationPresent(PostInitialize.class)) {
                continue;
            }

            try {
                method.invoke(controller);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
