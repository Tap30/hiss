package io.github.tap30.hiss.properties;

import io.github.tap30.hiss.key.Key;

import java.util.Base64;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Provides {@link HissProperties} from environment variables.
 * <br>
 * Here's the mapping of the <code>HissProperties</code> field to environment variables:
 * <br>
 * <ul>
 *     <li>
 *         <code>keys</code>:
 *         <ul>
 *             <li><code>HISS_KEYS_{Key ID}</code>: the base64 encoded representation of the key.</li>
 *             <li><code>HISS_KEYS_{Key ID}__HASH</code>: the hash of the key.</li>
 *         </ul>
 *     </li>
 *     <li>
 *         <code>defaultEncryptionKeyId</code>: <code>HISS_DEFAULT_ENCRYPTION_ALGORITHM</code>
 *     </li>
 *     <li>
 *         <code>defaultEncryptionAlgorithm</code>: <code>HISS_DEFAULT_ENCRYPTION_ALGORITHM</code>
 *     </li>
 *     <li>
 *         <code>defaultHashingKeyId</code>: <code>HISS_DEFAULT_HASHING_KEY_ID</code>
 *     </li>
 *     <li>
 *         <code>defaultHashingAlgorithm</code>: <code>HISS_DEFAULT_HASHING_ALGORITHM</code>
 *     </li>
 *     <li>
 *         <code>keyHashGenerationEnabled</code>: <code>HISS_KEY_HASH_GENERATION_ENABLED</code>
 *     </li>
 * </ul>
 *
 * @see HissProperties
 */
public class HissPropertiesFromEnvProvider implements HissPropertiesProvider {

    private static final String KEY_ENV_PREFIX = "HISS_KEYS_";
    private static final String KEY_HASH_ENV_POSTFIX = "___HASH";

    private static final Supplier<Map<String, String>> ENV_PROVIDER = System::getenv;

    @Override
    public Set<Key> getKeys() {
        var keys = new HashSet<Key>();
        ENV_PROVIDER.get().forEach((k, v) -> {
            if (k.startsWith(KEY_ENV_PREFIX) && !k.endsWith(KEY_HASH_ENV_POSTFIX)) {
                keys.add(Key.builder()
                        .id(k.replace(KEY_ENV_PREFIX, "").toLowerCase())
                        .key(Base64.getDecoder().decode(v))
                        .keyHash(ENV_PROVIDER.get().get(k + KEY_HASH_ENV_POSTFIX))
                        .build());
            }
        });
        return keys;
    }

    @Override
    public String getDefaultEncryptionKeyId() {
        return ENV_PROVIDER.get().get("HISS_DEFAULT_ENCRYPTION_KEY_ID");
    }

    @Override
    public String getDefaultEncryptionAlgorithm() {
        return ENV_PROVIDER.get().get("HISS_DEFAULT_ENCRYPTION_ALGORITHM");
    }

    @Override
    public String getDefaultHashingKeyId() {
        return ENV_PROVIDER.get().get("HISS_DEFAULT_HASHING_KEY_ID");
    }

    @Override
    public String getDefaultHashingAlgorithm() {
        return ENV_PROVIDER.get().get("HISS_DEFAULT_HASHING_ALGORITHM");
    }

    @Override
    public boolean isKeyHashGenerationEnabled() {
        return Boolean.parseBoolean(ENV_PROVIDER.get().get("HISS_KEY_HASH_GENERATION_ENABLED"));
    }

}
