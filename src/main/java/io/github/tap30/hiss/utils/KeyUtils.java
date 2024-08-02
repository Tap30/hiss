package io.github.tap30.hiss.utils;

import io.github.tap30.hiss.key.Key;

import java.util.Map;

public class KeyUtils {

    public static byte[] getKey(String keyId, Map<String, Key> keys) {
        var key = keys.get(keyId.toLowerCase());
        if (key == null) {
            throw new IllegalArgumentException("Key ID not found in keys map.");
        }
        return key.getKey();
    }

}
