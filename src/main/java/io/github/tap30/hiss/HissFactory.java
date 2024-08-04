package io.github.tap30.hiss;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.github.tap30.hiss.encryptor.*;
import io.github.tap30.hiss.hasher.Hasher;
import io.github.tap30.hiss.hasher.HissHasher;
import io.github.tap30.hiss.hasher.HmacSha256Hasher;
import io.github.tap30.hiss.hasher.TapsiHmacSha256Hasher;
import io.github.tap30.hiss.key.KeyHashGenerator;
import io.github.tap30.hiss.properties.HissProperties;
import io.github.tap30.hiss.properties.HissPropertiesValidator;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class HissFactory {

    private static final Logger logger = Logger.getLogger(HissFactory.class.getName());

    /**
     * Creates a Hiss instance with provided <code>HissProperties</code> and default encryptors and hashers.
     *
     * @param hissProperties the properties by which hiss will be instantiated;
     *                       {@link io.github.tap30.hiss.properties.HissPropertiesFromEnv}
     *                       or any custom implementation of
     *                       {@link io.github.tap30.hiss.properties.HissProperties}
     *                       can be used.
     * @return {@link Hiss} instance.
     * @throws IllegalArgumentException if the properties are not valid.
     */
    public static Hiss createHiss(HissProperties hissProperties) {
        return createHiss(hissProperties, Set.of(), Set.of());
    }

    /**
     * Creates a Hiss instance with provided <code>HissProperties</code> and encryptors and hashers.
     * <br>
     * Provided encryptors and hashers will be added alongside default ones.
     *
     * @param hissProperties the properties by which hiss will be instantiated;
     *                       {@link io.github.tap30.hiss.properties.HissPropertiesFromEnv}
     *                       or any custom implementation of
     *                       {@link io.github.tap30.hiss.properties.HissProperties}
     *                       can be used.
     * @param encryptors custom {@link Encryptor} implementations. Can be empty but not null.
     * @param hashers custom {@link Hasher} implementations. Can be empty but not null.
     * @return {@link Hiss} instance.
     * @throws IllegalArgumentException if the properties are not valid.
     */
    public static Hiss createHiss(HissProperties hissProperties,
                                  Set<Encryptor> encryptors,
                                  Set<Hasher> hashers) {
        Objects.requireNonNull(hissProperties);
        Objects.requireNonNull(encryptors);
        Objects.requireNonNull(hashers);

        var keyHashGenerator = new KeyHashGenerator(BCrypt.withDefaults(), BCrypt.verifyer());
        new HissPropertiesValidator(keyHashGenerator).validate(hissProperties);

        encryptors = addDefaultEncryptors(encryptors);
        hashers = addDefaultHashers(hashers);

        var hissEncryptor = new HissEncryptor(
                encryptors,
                hissProperties.getKeys(),
                hissProperties.getDefaultEncryptionAlgorithm(),
                hissProperties.getDefaultEncryptionKeyId()
        );
        var hissHasher = new HissHasher(
                hashers,
                hissProperties.getKeys(),
                hissProperties.getDefaultHashingAlgorithm(),
                hissProperties.getDefaultHashingKeyId()
        );
        var hissObjectEncryptor = new HissObjectEncryptor(hissEncryptor, hissHasher);

        logInitializingHiss(hissProperties, encryptors, hashers);
        if (hissProperties.isKeyHashGenerationEnabled()) {
            keyHashGenerator.generateAndLogHashes(hissProperties.getKeys().values());
        }

        return new Hiss(hissEncryptor, hissHasher, hissObjectEncryptor);
    }

    private static @NotNull Set<Encryptor> addDefaultEncryptors(Set<Encryptor> encryptors) {
        encryptors = new HashSet<>(encryptors);
        encryptors.add(new AesCbcPkcs5PaddingEncryptor());
        encryptors.add(new AesGcmNoPaddingEncryptor());
        encryptors.add(new TapsiAesCbcEncryptor());
        encryptors.add(new TapsiAesGcmEncryptor());
        encryptors = Collections.unmodifiableSet(encryptors);
        return encryptors;
    }

    private static @NotNull Set<Hasher> addDefaultHashers(Set<Hasher> hashers) {
        hashers = new HashSet<>(hashers);
        hashers.add(new HmacSha256Hasher());
        hashers.add(new TapsiHmacSha256Hasher());
        hashers = Collections.unmodifiableSet(hashers);
        return hashers;
    }

    private static void logInitializingHiss(HissProperties hissProperties,
                                     Set<Encryptor> encryptors,
                                     Set<Hasher> hashers) {
        logger.log(Level.INFO, "Hiss initialized:\n" +
                        "  Loaded Keys: {0}\n" +
                        "  Default Encryption Key ID: {1}\n" +
                        "  Default Encryption Algorithm: {2}\n" +
                        "  Default Hashing Key ID: {3}\n" +
                        "  Default Hashing Algorithm: {4}\n" +
                        "  Encryptors: {5}\n" +
                        "  Hashers: {6}\n",
                new Object[]{
                        hissProperties.getKeys().keySet(),
                        hissProperties.getDefaultEncryptionKeyId(),
                        hissProperties.getDefaultEncryptionAlgorithm(),
                        hissProperties.getDefaultHashingKeyId(),
                        hissProperties.getDefaultHashingAlgorithm(),
                        encryptors.stream().map(Encryptor::getName).collect(Collectors.toSet()),
                        hashers.stream().map(Hasher::getName).collect(Collectors.toSet())
                });
    }

}
