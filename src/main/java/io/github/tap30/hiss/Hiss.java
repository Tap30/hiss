package io.github.tap30.hiss;

import io.github.tap30.hiss.key.KeyHashGenerator;
import io.github.tap30.hiss.properties.HissProperties;
import io.github.tap30.hiss.utils.EncryptionUtils;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Hiss {

    private static final Logger logger = Logger.getLogger(Hiss.class.getName());

    private final HissEncryptor hissEncryptor;
    private final HissObjectEncryptor hissObjectEncryptor;

    Hiss(HissProperties hissProperties, KeyHashGenerator keyHashGenerator) {
        this.hissEncryptor = new HissEncryptor(hissProperties);
        this.hissObjectEncryptor = new HissObjectEncryptor(this.hissEncryptor);
        logger.log(Level.INFO, "Hiss initialized:\n" +
                        "  Loaded Keys: {0}\n" +
                        "  Default Encryption Key ID: {1}\n" +
                        "  Default Encryption Algorithm: {2}\n" +
                        "  Default Hashing Key ID: {3}\n" +
                        "  Default Hashing Algorithm: {4}",
                new Object[]{
                        hissProperties.getKeys().keySet(),
                        hissProperties.getDefaultEncryptionKeyId(),
                        hissProperties.getDefaultEncryptionAlgorithm(),
                        hissProperties.getDefaultHashingKeyId(),
                        hissProperties.getDefaultHashingAlgorithm()
                });
        if (hissProperties.isKeyHashGenerationEnabled()) {
            keyHashGenerator.generateAndLogHashes(hissProperties.getKeys().values());
        }
    }

    /**
     * Encrypts the provided content with default key and default algorithm.
     *
     * @param content the content to be encrypted.
     * @return encrypted content or null if the content is null.
     */
    public String encrypt(@Nullable String content) {
        return this.encrypt(content, "");
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
        return this.hissEncryptor.encrypt(content, pattern);
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
        return this.hissEncryptor.decrypt(content);
    }

    /**
     * Hashes the provided content with default key and default algorithm.
     *
     * @param content the content to be hashed.
     * @return hashed content or null if the content is null.
     */
    public String hash(@Nullable String content) {
        return this.hash(content, "");
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
        return this.hissEncryptor.hash(content, pattern);
    }

    /**
     * Tells whether the provider content is encrypted.
     *
     * @param content the content.
     * @return <code>true</code> if the content or parts of it is encrypted.
     */
    public boolean isEncrypted(@Nullable String content) {
        return EncryptionUtils.isHavingEncryptionPattern(content);
    }

    /**
     * Tells whether the provider content is hashed.
     *
     * @param content the content.
     * @return <code>true</code> if the content or parts of it is hashed.
     */
    public boolean isHashed(@Nullable String content) {
        return EncryptionUtils.isHavingEncryptionPattern(content);
    }

    /**
     * Encrypts and hashes fields of object annotated with {@link Encrypted} and {@link EncryptedInside}.
     *
     * @param object the annotated object; no action is taken on null objects.
     */
    public void encryptObject(@Nullable Object object) {
        this.hissObjectEncryptor.encryptObject(object);
    }

    /**
     * Decrypts fields of object annotated with {@link Encrypted} and {@link EncryptedInside}.
     *
     * @param object the annotated object; no action is taken on null objects.
     */
    public void decryptObject(@Nullable Object object) {
        this.hissObjectEncryptor.decryptObject(object);
    }

}
