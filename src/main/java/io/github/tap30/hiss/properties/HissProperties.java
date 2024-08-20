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

@Builder
@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class HissProperties {

    Map<String, Key> keys;
    String defaultEncryptionKeyId;
    String defaultEncryptionAlgorithm;
    String defaultHashingKeyId;
    String defaultHashingAlgorithm;
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
