package io.github.tap30.hiss.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReflectionUtilsTest {

    @Test
    void testInvokeGetter() throws NoSuchMethodException {
        // Given
        var aClassInstance = new AClass();
        var nameGetterMethod = aClassInstance.getClass().getDeclaredMethod("getName");

        // When
        var content = ReflectionUtils.invokeSupplier(aClassInstance, nameGetterMethod, String.class);

        // Then
        assertEquals("Mamad", content);
    }

    @Test
    void testInvokeGetter_whenTypeNotMatches() throws NoSuchMethodException {
        // Given
        var aClassInstance = new AClass();
        var nameGetterMethod = aClassInstance.getClass().getDeclaredMethod("getName");

        // When & Then
        assertThrows(ClassCastException.class, () -> ReflectionUtils.invokeSupplier(aClassInstance, nameGetterMethod, Integer.class));
    }

    public static class AClass {
        public String getName() {
            return "Mamad";
        }
    }

}