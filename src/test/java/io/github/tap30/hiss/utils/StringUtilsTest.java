package io.github.tap30.hiss.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void hasText_whenTextIsNull() {
        assertFalse(StringUtils.hasText(null));
    }

    @Test
    void hasText_whenTextIsEmpty() {
        assertFalse(StringUtils.hasText(""));
        assertFalse(StringUtils.hasText("  "));
    }

    @Test
    void requireNonBlank() {
        assertEquals("salam", StringUtils.requireNonBlank("salam"));
    }

    @Test
    void requireNonBlank_whenTextIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> StringUtils.requireNonBlank("   "));
    }

    @Test
    void toLowerCase() {
        assertEquals("salam", StringUtils.toLowerCase("SALAM"));
    }

    @Test
    void toLowerCase_whenTextIsNull() {
        assertNull(StringUtils.toLowerCase(null));
    }

    @Test
    void capitalizeFirstLetter() {
        assertEquals("Salam", StringUtils.capitalizeFirstLetter("salam"));
    }

    @Test
    void capitalizeFirstLetter_whenTextIsNull() {
        assertNull(StringUtils.capitalizeFirstLetter(null));
    }

}