package pl.pollub.frontend.injector;

import lombok.Getter;

import java.lang.reflect.Field;
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

    public static void inject(Object instance) {
        for (Field field : instance.getClass().getFields()) {
            Object objectToSet = SimpleInjector.getInstance(field.getType());
            if (objectToSet == null) {
                continue;
            }

            try {
                field.setAccessible(true);
                field.set(instance, objectToSet);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
