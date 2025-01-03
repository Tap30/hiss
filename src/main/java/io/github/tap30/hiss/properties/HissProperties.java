package io.github.tap30.hiss.properties;

import io.github.tap30.hiss.key.Key;
import io.github.tap30.hiss.utils.StringUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Properties by which an Hiss instance can be created.
 */
@Builder
@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class HissProperties {

    /**
     * Pairs of key ID (name) to key.
     */
    Map<String, Key> keys;
    /**
     * The key ID of the key by which encryption will be done. It must exist in `keys` map.
     */
    String defaultEncryptionKeyId;
    /**
     * The algorithm name by which encryption will be done.
     * It must exist among default or custom encryption algorithms.
     */
    String defaultEncryptionAlgorithm;
    /**
     * The key ID of the key by which hashing will be done. It must exist in `keys` map.
     */
    String defaultHashingKeyId;
    /**
     * The algorithm name by which hashing will be done.
     * It must exist among default or custom hashing algorithms.
     */
    String defaultHashingAlgorithm;
    /**
     * Whether to generate keys' hashes on Hiss instantiation.
     */
    boolean keyHashGenerationEnabled;

    /**
     * See {@link HissPropertiesFromEnvProvider}.
     */
    public static HissProperties fromEnv() {
        return withProvider(new HissPropertiesFromEnvProvider());
    }

    public static HissProperties withProvider(HissPropertiesProvider provider) {
        return builder()
                .keys(provider.getKeys())
                .defaultEncryptionKeyId(provider.getDefaultEncryptionKeyId())
                .defaultEncryptionAlgorithm(provider.getDefaultEncryptionAlgorithm())
                .defaultHashingKeyId(provider.getDefaultHashingKeyId())
                .defaultHashingAlgorithm(provider.getDefaultHashingAlgorithm())
                .keyHashGenerationEnabled(provider.isKeyHashGenerationEnabled())
                .build();
    }

    public static class HissPropertiesBuilder {
        public HissPropertiesBuilder keys(Set<Key> keys) {
            this.keys = keys.stream()
                    .collect(Collectors.toMap(k -> StringUtils.toLowerCase(k.getId()), Function.identity()));
            return this;
        }

        public HissPropertiesBuilder defaultEncryptionKeyId(String defaultEncryptionKeyId) {
            this.defaultEncryptionKeyId = StringUtils.toLowerCase(defaultEncryptionKeyId);
            return this;
        }

        public HissPropertiesBuilder defaultEncryptionAlgorithm(String defaultEncryptionAlgorithm) {
            this.defaultEncryptionAlgorithm = StringUtils.toLowerCase(defaultEncryptionAlgorithm);
            return this;
        }

        public HissPropertiesBuilder defaultHashingKeyId(String defaultHashingKeyId) {
            this.defaultHashingKeyId = StringUtils.toLowerCase(defaultHashingKeyId);
            return this;
        }

        public HissPropertiesBuilder defaultHashingAlgorithm(String defaultHashingAlgorithm) {
            this.defaultHashingAlgorithm = StringUtils.toLowerCase(defaultHashingAlgorithm);
            return this;
        }
    }


}
