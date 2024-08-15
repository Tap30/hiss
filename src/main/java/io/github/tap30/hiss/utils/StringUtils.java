package io.github.tap30.hiss.utils;

public class StringUtils {

    public static boolean hasText(String text) {
        return text != null && !text.isBlank();
    }

    public static String requireNonBlank(String text) {
        if (!hasText(text)) {
            throw new IllegalArgumentException();
        }
        return text;
    }

    public static String toLowerCase(String text) {
        if (!hasText(text)) {
            return text;
        }
        return text.toLowerCase();
    }

    public static String capitalizeFirstLetter(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

}
