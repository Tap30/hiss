package io.github.tap30.hiss.utils;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class KeyUtils {

    public static Map<String, byte[]> convertBase64KeysToByteArrayKeys(Map<String, String> keys) {
        var decodedKeys = new HashMap<String, byte[]>();
        keys.forEach((k,v) -> decodedKeys.put(k.toLowerCase(), Base64.getDecoder().decode(v)));
        return Collections.unmodifiableMap(decodedKeys);
    }

    public static byte[] getKey(String keyId, Map<String, byte[]> keys) {
        var key = keys.get(keyId.toLowerCase());
        if (key == null) {
            throw new IllegalArgumentException("Key ID not found in keys map.");
        }
        return key;
    }

}
