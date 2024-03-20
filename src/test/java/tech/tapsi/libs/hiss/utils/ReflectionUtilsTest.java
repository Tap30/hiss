package tech.tapsi.libs.hiss.utils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionUtilsTest {

    @Test
    void testInvokeGetter() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Given
        var aClassInstance = new AClass();
        var nameGetterMethod = aClassInstance.getClass().getDeclaredMethod("getName");

        // When
        var content = ReflectionUtils.invokeGetter(nameGetterMethod, aClassInstance, String.class);

        // Then
        assertEquals("Mamad", content);
    }

    @Test
    void testInvokeGetter_whenTypeNotMatches() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Given
        var aClassInstance = new AClass();
        var nameGetterMethod = aClassInstance.getClass().getDeclaredMethod("getName");

        // When & Then
        assertThrows(ClassCastException.class, () -> ReflectionUtils.invokeGetter(nameGetterMethod, aClassInstance, Integer.class));
    }

    public static class AClass {
        public String getName() {
            return "Mamad";
        }
    }

}