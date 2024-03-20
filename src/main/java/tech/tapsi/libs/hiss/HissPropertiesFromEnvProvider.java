package tech.tapsi.libs.hiss;

import java.util.HashMap;

/**
 * Sample Envs:
 * <br>
 * <code>
 *     HISS_KEYS_KEY1: AAAAAAAAAAAAAAAAAAAAAA==
 *     <br>
 *     HISS_KEYS_KEY2: AAAAAAAAAAAAAAAAAAAAAA==
 *     <br>
 *     HISS_DEFAULT_ENCRYPTION_KEY_ID: key1
 *     <br>
 *     HISS_DEFAULT_ENCRYPTION_ALGORITHM: aes-128-gcm
 *     <br>
 *     HISS_DEFAULT_HASHING_KEY_ID: key2
 *     <br>
 *     HISS_DEFAULT_HASHING_ALGORITHM: hmac-sha256
 * </code>
 */
public class HissPropertiesFromEnvProvider implements HissPropertiesProvider {

    private static final String KEY_ENV_PREFIX = "HISS_KEYS_";

    @Override
    public HissProperties getProperties() {
        var keys = new HashMap<String, String>();
        System.getenv().forEach((k, v) -> {
            if (k.startsWith(KEY_ENV_PREFIX)) {
                keys.put(k.replace(KEY_ENV_PREFIX, "").toLowerCase(), v);
            }
        });
        return HissProperties.fromBase64EncodedKeys(
                keys,
                System.getenv("HISS_DEFAULT_ENCRYPTION_KEY_ID"),
                System.getenv("HISS_DEFAULT_ENCRYPTION_ALGORITHM"),
                System.getenv("HISS_DEFAULT_HASHING_KEY_ID"),
                System.getenv("HISS_DEFAULT_HASHING_ALGORITHM")
        );
    }

}
