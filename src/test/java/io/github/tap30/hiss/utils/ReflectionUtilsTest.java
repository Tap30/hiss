package io.github.tap30.hiss.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReflectionUtilsTest {

    @Test
    void testInvokeSupplierMethod() throws NoSuchMethodException {
        // Given
        var aClassInstance = new AClass();
        var nameGetterMethod = aClassInstance.getClass().getDeclaredMethod("getName");

        // When
        var content = ReflectionUtils.invokeSupplierMethod(aClassInstance, nameGetterMethod, String.class);

        // Then
        assertEquals("Mamad", content);
    }

    @Test
    void testInvokeSupplierMethod_whenTypeNotMatches() throws NoSuchMethodException {
        // Given
        var aClassInstance = new AClass();
        var nameGetterMethod = aClassInstance.getClass().getDeclaredMethod("getName");

        // When & Then
        assertThrows(ClassCastException.class, () -> ReflectionUtils.invokeSupplierMethod(aClassInstance, nameGetterMethod, Integer.class));
    }

    public static class AClass {
        public String getName() {
            return "Mamad";
        }
    }

    public static class Level1 {
        private String level1Field;
        public void level1Method() {
        }
    }

    public static class Level2 extends Level1 {
        private String level2Field;
        public void level2Method() {
        }
    }

    public static class Level3 extends Level2 {
        private String level3Field;
        public void level3Method() {
        }
    }

}