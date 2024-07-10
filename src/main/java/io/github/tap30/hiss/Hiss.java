package io.github.tap30.hiss;

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

    Hiss(HissProperties hissProperties) {
        hissProperties.validate();
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
    }

    public String encrypt(String content) {
        return this.encrypt(content, "");
    }

    public String encrypt(String content, @Language("regexp") @Nullable String pattern) {
        return this.hissEncryptor.encrypt(content, pattern);
    }

    public String decrypt(String content) {
        return this.hissEncryptor.decrypt(content);
    }

    public String hash(String content) {
        return this.hash(content, "");
    }

    public String hash(String content, @Language("regexp") @Nullable String pattern) {
        return this.hissEncryptor.hash(content, pattern);
    }

    public boolean isEncrypted(String content) {
        return EncryptionUtils.isHavingEncryptionPattern(content);
    }

    public boolean isHashed(String content) {
        return EncryptionUtils.isHavingEncryptionPattern(content);
    }

    public void encryptObject(Object object) {
        this.hissObjectEncryptor.encryptObject(object);
    }

    public void decryptObject(Object object) {
        this.hissObjectEncryptor.decryptObject(object);
    }

}
