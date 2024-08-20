package io.github.tap30.hiss.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionUtilsTest {

    @Test
    void invokeSupplierMethod() throws NoSuchMethodException {
        // Given
        var aClassInstance = new AClass();
        var nameGetterMethod = aClassInstance.getClass().getDeclaredMethod("getName");

        // When
        var content = ReflectionUtils.invokeSupplierMethod(aClassInstance, nameGetterMethod, String.class);

        // Then
        assertEquals("Mamad", content);
    }

    @Test
    void invokeSupplierMethod_whenTypeNotMatches() throws NoSuchMethodException {
        // Given
        var aClassInstance = new AClass();
        var nameGetterMethod = aClassInstance.getClass().getDeclaredMethod("getName");

        // When & Then
        assertThrows(ClassCastException.class, () -> ReflectionUtils.invokeSupplierMethod(aClassInstance, nameGetterMethod, Integer.class));
    }

    @Test
    void getAllFields() {
        // Given
        var instance = new Level3();

        // When
        var fields = ReflectionUtils.getAllFields(instance.getClass());

        // Then
        assertEquals(3, fields.size());
        assertEquals("level3Field", fields.get(0).getName());
        assertEquals("level2Field", fields.get(1).getName());
        assertEquals("level1Field", fields.get(2).getName());
    }

    @Test
    void getMethod() {
        // Given
        var instance = new Level3();

        // When
        var level1Method = ReflectionUtils.getMethod(instance.getClass(), "level1Method");
        var level2Method = ReflectionUtils.getMethod(instance.getClass(), "level2Method");
        var level3Method = ReflectionUtils.getMethod(instance.getClass(), "level3Method");

        // Then
        assertNotNull(level1Method);
        assertNotNull(level2Method);
        assertNotNull(level3Method);
        assertEquals("level1Method", level1Method.getName());
        assertEquals("level2Method", level2Method.getName());
        assertEquals("level3Method", level3Method.getName());
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