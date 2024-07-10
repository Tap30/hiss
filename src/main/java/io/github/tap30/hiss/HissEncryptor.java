package io.github.tap30.hiss;

import io.github.tap30.hiss.properties.HissProperties;
import io.github.tap30.hiss.utils.EncryptionUtils;
import io.github.tap30.hiss.utils.HashingUtils;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;

class HissEncryptor {

    private final HissProperties hissProperties;

    public HissEncryptor(HissProperties hissProperties) {
        this.hissProperties = hissProperties;
    }

    public String encrypt(String content, @Language("regexp") @Nullable String pattern) {
        try {
            return EncryptionUtils.encrypt(
                    hissProperties.getDefaultEncryptionKeyId(),
                    hissProperties.getKeys(),
                    hissProperties.getDefaultEncryptionAlgorithm(),
                    content, pattern
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String content)  {
        try {
            return EncryptionUtils.decrypt(hissProperties.getKeys(), content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String hash(String content, @Language("regexp") @Nullable String pattern) {
        try {
            return HashingUtils.hash(
                    hissProperties.getDefaultHashingKeyId(),
                    hissProperties.getKeys(),
                    hissProperties.getDefaultHashingAlgorithm(),
                    content,
                    pattern
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
