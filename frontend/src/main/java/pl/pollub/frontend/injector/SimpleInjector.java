package pl.pollub.frontend.injector;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class SimpleInjector {
    @Getter
    private static final Map<Class<?>, Object> instances = new HashMap<>();

    public static void addInstance(Object instance) {
        instances.put(instance.getClass(), instance);
    }

    public static <T> T getInstance(Class<T> clazz) {
        return clazz.cast(instances.get(clazz));
    }
}
