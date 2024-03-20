package tech.tapsi.libs.hiss;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;
import tech.tapsi.libs.hiss.utils.EncryptionUtils;
import tech.tapsi.libs.hiss.utils.HashingUtils;

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
