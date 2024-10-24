package io.github.tap30.hiss.properties;

import io.github.tap30.hiss.key.Key;

import java.util.Set;

public interface HissPropertiesProvider {
    Set<Key> getKeys();
    String getDefaultEncryptionKeyId();
    String getDefaultEncryptionAlgorithm();
    String getDefaultHashingKeyId();
    String getDefaultHashingAlgorithm();
    boolean isKeyHashGenerationEnabled();
}
