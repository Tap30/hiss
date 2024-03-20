package tech.tapsi.libs.hiss.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReflectionUtils {


    public static <T> T invokeGetter(Method getter, Object object, Class<T> targetType) {
        Object content;
        try {
            content = getter.invoke(object);
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
                    "Cast error for content of method %s: wanted %s but got %s", getter.getName(), targetType.getName(), content.getClass().getName()
            ));
        }
    }

    public static List<Field> getAllFields(Class<?> clazz) {
        var objectFields = new ArrayList<Field>();
        Class<?> objectClass = clazz;
        while (objectClass != null) {
            objectFields.addAll(Arrays.asList(objectClass.getDeclaredFields()));
            objectClass = objectClass.getSuperclass();
        }
        return Collections.unmodifiableList(objectFields);
    }

}
