package io.github.tap30.hiss.properties;

import io.github.tap30.hiss.key.Key;
import lombok.Setter;

import java.util.Base64;
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
    private static final String KEY_HASH_ENV_POSTFIX = "___HASH";

    @Setter // todo: make it package private
    private Supplier<Map<String, String>> envProvider = System::getenv;

    @Override
    public Map<String, Key> loadKeys() {
        var keys = new HashMap<String, Key>();
        envProvider.get().forEach((k, v) -> {
            if (k.startsWith(KEY_ENV_PREFIX) && !k.endsWith(KEY_HASH_ENV_POSTFIX)) {
                var id = k.replace(KEY_ENV_PREFIX, "").toLowerCase();
                keys.put(id, Key.builder()
                        .id(id)
                        .key(Base64.getDecoder().decode(v))
                        .keyHash(envProvider.get().get(k + KEY_HASH_ENV_POSTFIX))
                        .build());
            }
        });
        return keys;
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

    @Override
    protected boolean loadKeyHashGenerationEnabled() {
        return Boolean.parseBoolean(envProvider.get().get("HISS_KEY_HASH_GENERATION_ENABLED"));
    }
}
