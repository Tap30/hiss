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

}
