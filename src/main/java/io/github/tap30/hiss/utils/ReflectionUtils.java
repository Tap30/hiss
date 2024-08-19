package io.github.tap30.hiss.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReflectionUtils {

    public static <T> T invokeSupplierMethod(Object object, Method supplier, Class<T> targetType) {
        Object content;
        try {
            content = supplier.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        if (content == null) {
            return null;
        }
        if (targetType.isAssignableFrom(content.getClass())) {
            @SuppressWarnings("unchecked")
            var castedContent = (T) content;
            return castedContent;
        } else {
            throw new ClassCastException(String.format(
                    "Cast error for content of method %s: wanted %s but got %s",
                    supplier.getName(), targetType.getName(), content.getClass().getName()
            ));
        }
    }

    public static List<Field> getAllFields(Class<?> clazz) {
        var objectFields = new ArrayList<Field>();
        for (var objectClass = clazz; objectClass != null; objectClass = objectClass.getSuperclass()) {
            objectFields.addAll(Arrays.asList(objectClass.getDeclaredFields()));
        }
        return Collections.unmodifiableList(objectFields);
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null) {
                return getMethod(clazz.getSuperclass(), methodName, parameterTypes);
            }
            throw new RuntimeException(e);
        }
    }

}
