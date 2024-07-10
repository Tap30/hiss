package io.github.tap30.hiss;

import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

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

    @Setter
    private Supplier<Map<String, String>> envProvider = System::getenv;

    @Override
    public HissProperties getProperties() {
        var keys = new HashMap<String, String>();
        var envs = envProvider.get();
        envs.forEach((k, v) -> {
            if (k.startsWith(KEY_ENV_PREFIX)) {
                keys.put(k.replace(KEY_ENV_PREFIX, "").toLowerCase(), v);
            }
        });
        return HissProperties.fromBase64EncodedKeys(
                keys,
                envs.get("HISS_DEFAULT_ENCRYPTION_KEY_ID"),
                envs.get("HISS_DEFAULT_ENCRYPTION_ALGORITHM"),
                envs.get("HISS_DEFAULT_HASHING_KEY_ID"),
                envs.get("HISS_DEFAULT_HASHING_ALGORITHM")
        );
    }

}
