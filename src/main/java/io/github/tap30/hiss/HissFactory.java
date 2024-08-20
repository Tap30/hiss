package io.github.tap30.hiss;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.github.tap30.hiss.encryptor.Encryptor;
import io.github.tap30.hiss.encryptor.impl.AesCbcPkcs5PaddingEncryptor;
import io.github.tap30.hiss.encryptor.impl.AesGcmNoPaddingEncryptor;
import io.github.tap30.hiss.encryptor.impl.TapsiAesCbcEncryptor;
import io.github.tap30.hiss.encryptor.impl.TapsiAesGcmEncryptor;
import io.github.tap30.hiss.hasher.Hasher;
import io.github.tap30.hiss.hasher.impl.HmacSha256Hasher;
import io.github.tap30.hiss.hasher.impl.TapsiHmacSha256Hasher;
import io.github.tap30.hiss.key.KeyHashGenerator;
import io.github.tap30.hiss.properties.HissProperties;
import io.github.tap30.hiss.properties.HissPropertiesProvider;
import io.github.tap30.hiss.properties.HissPropertiesValidator;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class HissFactory {

    private static final Logger logger = Logger.getLogger(HissFactory.class.getName());

    /**
     * Creates a Hiss instance with provided <code>HissProperties</code> and default encryptors and hashers.
     *
     * @param hissProperties the properties by which hiss will be instantiated;
     *                       use {@link HissProperties#fromEnv()}
     *                       or {@link HissProperties#builder()}
     *                       or {@link HissProperties#withProvider(HissPropertiesProvider)}
     * @return {@link Hiss} instance.
     * @throws IllegalArgumentException if the properties are not valid.
     */
    public static Hiss createHiss(@NotNull HissProperties hissProperties) {
        return createHiss(hissProperties, Set.of(), Set.of());
    }

    /**
     * Creates a Hiss instance with provided <code>HissProperties</code> and encryptors and hashers.
     * <br>
     * Provided encryptors and hashers will be added alongside default ones.
     *
     * @param hissProperties the properties by which hiss will be instantiated;
     *                       use {@link HissProperties#fromEnv()}
     *                       or {@link HissProperties#builder()}
     *                       or {@link HissProperties#withProvider(HissPropertiesProvider)}
     * @param encryptors     custom {@link Encryptor} implementations. Can be empty but not null.
     * @param hashers        custom {@link Hasher} implementations. Can be empty but not null.
     * @return {@link Hiss} instance.
     * @throws IllegalArgumentException if the properties are not valid.
     */
    public static Hiss createHiss(@NotNull HissProperties hissProperties,
                                  @NotNull Set<Encryptor> encryptors,
                                  @NotNull Set<Hasher> hashers) {
        Objects.requireNonNull(hissProperties);
        Objects.requireNonNull(encryptors);
        Objects.requireNonNull(hashers);

        var encryptorsMap = addDefaultEncryptors(encryptors)
                .stream().collect(Collectors.toMap(e -> e.getName().toLowerCase(), e -> e));
        var hashersMap = addDefaultHashers(hashers)
                .stream().collect(Collectors.toMap(h -> h.getName().toLowerCase(), h -> h));

        var keyHashGenerator = new KeyHashGenerator(BCrypt.withDefaults(), BCrypt.verifyer());
        new HissPropertiesValidator(keyHashGenerator, encryptorsMap, hashersMap).validate(hissProperties);

        var hissEncryptor = new HissEncryptor(
                encryptorsMap,
                hissProperties.getKeys(),
                hissProperties.getDefaultEncryptionAlgorithm(),
                hissProperties.getDefaultEncryptionKeyId()
        );
        var hissHasher = new HissHasher(
                hashersMap,
                hissProperties.getKeys(),
                hissProperties.getDefaultHashingAlgorithm(),
                hissProperties.getDefaultHashingKeyId()
        );
        var hissObjectEncryptor = new HissObjectEncryptor(hissEncryptor, hissHasher);

        logInitializingHiss(hissProperties, encryptorsMap, hashersMap);
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
                                            Map<String, Encryptor> encryptors,
                                            Map<String, Hasher> hashers) {
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
                        encryptors.keySet(),
                        hashers.keySet()
                });
    }

}
