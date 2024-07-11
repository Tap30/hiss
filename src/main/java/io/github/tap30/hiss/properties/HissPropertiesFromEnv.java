package io.github.tap30.hiss.properties;

import io.github.tap30.hiss.utils.KeyUtils;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Sample Envs:
 * <br>
 * <code>
 * HISS_KEYS_KEY1: AAAAAAAAAAAAAAAAAAAAAA==
 * <br>
 * HISS_KEYS_KEY2: AAAAAAAAAAAAAAAAAAAAAA==
 * <br>
 * HISS_DEFAULT_ENCRYPTION_KEY_ID: key1
 * <br>
 * HISS_DEFAULT_ENCRYPTION_ALGORITHM: aes-128-gcm
 * <br>
 * HISS_DEFAULT_HASHING_KEY_ID: key2
 * <br>
 * HISS_DEFAULT_HASHING_ALGORITHM: hmac-sha256
 * </code>
 */
public class HissPropertiesFromEnv extends HissProperties {

    private static final String KEY_ENV_PREFIX = "HISS_KEYS_";

    @Setter
    private Supplier<Map<String, String>> envProvider = System::getenv;

    @Override
    public Map<String, byte[]> loadKeys() {
        var keys = new HashMap<String, String>();
        envProvider.get().forEach((k, v) -> {
            if (k.startsWith(KEY_ENV_PREFIX)) {
                keys.put(k.replace(KEY_ENV_PREFIX, "").toLowerCase(), v);
            }
        });
        return KeyUtils.convertBase64KeysToByteArrayKeys(keys);
    }

    @Override
    public String loadDefaultEncryptionKeyId() {
        return envProvider.get().get("HISS_DEFAULT_ENCRYPTION_KEY_ID");
    }

    @Override
    public String loadDefaultEncryptionAlgorithm() {
        return envProvider.get().get("HISS_DEFAULT_ENCRYPTION_ALGORITHM");
    }

    @Override
    public String loadDefaultHashingKeyId() {
        return envProvider.get().get("HISS_DEFAULT_HASHING_KEY_ID");
    }

    @Override
    public String loadDefaultHashingAlgorithm() {
        return envProvider.get().get("HISS_DEFAULT_HASHING_ALGORITHM");
    }
}
