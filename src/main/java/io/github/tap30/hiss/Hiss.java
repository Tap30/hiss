package io.github.tap30.hiss;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Hiss {

    private final HissEncryptor hissEncryptor;
    private final HissHasher hissHasher;
    private final HissObjectEncryptor hissObjectEncryptor;

    Hiss(HissEncryptor hissEncryptor,
         HissHasher hissHasher,
         HissObjectEncryptor hissObjectEncryptor) {
        this.hissEncryptor = Objects.requireNonNull(hissEncryptor);
        this.hissHasher = Objects.requireNonNull(hissHasher);
        this.hissObjectEncryptor = Objects.requireNonNull(hissObjectEncryptor);
    }

    /**
     * Encrypts the provided content with default key and default algorithm.
     *
     * @param content the content to be encrypted.
     * @return encrypted content or null if the content is null.
     */
    public String encrypt(@Nullable String content) {
        return encrypt(content, "");
    }

    /**
     * Encrypts parts of the provided content which match with the provided pattern
     * with default key and default algorithm.
     *
     * @param content the content to be encrypted.
     * @param pattern the pattern in regex format; null or empty pattern means to match all.
     * @return encrypted content or null if the content is null.
     */
    public String encrypt(@Nullable String content, @Language("regexp") @Nullable String pattern) {
        try {
            return hissEncryptor.encrypt(content, pattern);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Decrypts the provided content; if the content is encrypted partially (with pattern),
     * only the encrypted parts will be decrypted.
     * <br>Key ID and algorithm must be among loaded/supported keys and algorithms.
     *
     * @param content the content to be decrypted.
     * @return decrypted content or null if the content is null.
     * @throws IllegalArgumentException if key ID or algorithm is not loaded/supported.
     */
    public String decrypt(@Nullable String content) {
        try {
            return hissEncryptor.decrypt(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Hashes the provided content with default key and default algorithm.
     *
     * @param content the content to be hashed.
     * @return hashed content or null if the content is null.
     */
    public String hash(@Nullable String content) {
        return hash(content, "");
    }

    /**
     * Hashes parts of the provided content which match with the provided pattern
     * with default key and default algorithm.
     *
     * @param content the content to be hashed.
     * @param pattern the pattern in regex format; null or empty pattern means to match all.
     * @return hashed content or null if the content is null.
     */
    public String hash(@Nullable String content, @Language("regexp") @Nullable String pattern) {
        try {
            return hissHasher.hash(content, pattern);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tells whether the provider content is encrypted.
     *
     * @param content the content.
     * @return <code>true</code> if the content or parts of it is encrypted.
     */
    public boolean isEncrypted(@Nullable String content) {
        return hissEncryptor.isEncrypted(content);
    }

    /**
     * Tells whether the provider content is hashed.
     *
     * @param content the content.
     * @return <code>true</code> if the content or parts of it is hashed.
     */
    public boolean isHashed(@Nullable String content) {
        return hissHasher.isHashed(content);
    }

    /**
     * Encrypts and hashes fields of object annotated with {@link Encrypted} and {@link EncryptedInside}.
     *
     * @param object the annotated object; no action is taken on null objects.
     */
    public void encryptObject(@Nullable Object object) {
        hissObjectEncryptor.encryptObject(object);
    }

    /**
     * Decrypts fields of object annotated with {@link Encrypted} and {@link EncryptedInside}.
     *
     * @param object the annotated object; no action is taken on null objects.
     */
    public void decryptObject(@Nullable Object object) {
        hissObjectEncryptor.decryptObject(object);
    }

}
