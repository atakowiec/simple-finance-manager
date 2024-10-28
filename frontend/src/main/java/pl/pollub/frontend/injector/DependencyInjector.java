package pl.pollub.frontend.injector;

import lombok.Getter;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import pl.pollub.frontend.annotation.PostInitialize;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class DependencyInjector {
    @Getter
    private final Map<Class<?>, Object> instancesMap = new HashMap<>();

    private DependencyInjector() {
        instancesMap.put(DependencyInjector.class, this);
        this.begin();
    }

    public static DependencyInjector init() {
        return new DependencyInjector();
    }

    private <T> T getInstance(Class<T> clazz) {
        return clazz.cast(instancesMap.get(clazz));
    }

    public Collection<Object> getInstances() {
        return instancesMap.values();
    }

    private void begin() {
        Reflections reflections = new Reflections("pl.pollub.frontend", Scanners.TypesAnnotated);

        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Injectable.class);

        for (Class<?> clazz : annotatedClasses) {
            addInstance(clazz);
        }

        for (Object instance : instancesMap.values()) {
            manualInject(instance);
        }

        for (Object instance : instancesMap.values()) {
            runPostInitialize(instance);
        }
    }

    public void addInstance(Class<?> clazz) {
        try {
            instancesMap.put(clazz, clazz.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            throw new RuntimeException("Cannot create instance of class: " + clazz.getName(), e);
        }
    }

    public void manualInject(Object instance) {
        manualInject(instance, instance.getClass());

        Class<?> clazz = instance.getClass();
        while (clazz.getSuperclass() != Object.class) {
            clazz = clazz.getSuperclass();
            manualInject(instance, clazz);
        }
    }

    private void manualInject(Object instance, Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
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

    public void runPostInitialize(Object controller) {
        runPostInitialize(controller, controller.getClass());

        Class<?> clazz = controller.getClass();
        while (clazz.getSuperclass() != Object.class) {
            clazz = clazz.getSuperclass();
            runPostInitialize(controller, clazz);
        }
    }

    private void runPostInitialize(Object object, Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(PostInitialize.class)) {
                continue;
            }

            try {
                method.setAccessible(true);
                method.invoke(object);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
