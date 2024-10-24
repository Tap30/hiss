package io.github.tap30.hiss.properties;

import io.github.tap30.hiss.encryptor.Encryptor;
import io.github.tap30.hiss.hasher.Hasher;
import io.github.tap30.hiss.key.KeyHashGenerator;
import io.github.tap30.hiss.utils.StringUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class HissPropertiesValidator {

    private final KeyHashGenerator keyHashGenerator;
    private final Map<String, Encryptor> encryptors;
    private final Map<String, Hasher> hashers;

    public HissPropertiesValidator(KeyHashGenerator keyHashGenerator,
                                   Map<String, Encryptor> encryptors,
                                   Map<String, Hasher> hashers) {
        this.keyHashGenerator = Objects.requireNonNull(keyHashGenerator);
        this.encryptors = Objects.requireNonNull(encryptors);
        this.hashers = Objects.requireNonNull(hashers);
    }

    public void validate(HissProperties hissProperties) {
        var errors = new ArrayList<String>();
        validateKeys(hissProperties, errors);
        validateDefaultEncryptionKeyAndAlgorithm(hissProperties, errors);
        validateDefaultHashingKeyAndAlgorithm(hissProperties, errors);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Hiss properties are not valid: " + String.join("; ", errors));
        }
    }

    private void validateKeys(HissProperties hissProperties, ArrayList<String> errors) {
        if (hissProperties.getKeys() == null || hissProperties.getKeys().isEmpty()) {
            errors.add("Keys are empty");
        } else {
            hissProperties.getKeys().forEach((k, v) -> {
                if (k == null || v == null || v.getKey() == null || v.getKey().length == 0) {
                    errors.add("Key " + k + " is empty");
                }
            });
            var mismatches = keyHashGenerator.validateKeyHashes(hissProperties.getKeys().values());
            if (!mismatches.isEmpty()) {
                errors.add("Key(s) " + mismatches + " did not match with their hashes");
            }
        }
    }

    private void validateDefaultEncryptionKeyAndAlgorithm(HissProperties hissProperties, ArrayList<String> errors) {
        if (!StringUtils.hasText(hissProperties.getDefaultEncryptionKeyId())) {
            errors.add("Default encryption key ID is not defined");
        }
        if (hissProperties.getKeys() != null
                && !hissProperties.getKeys().containsKey(hissProperties.getDefaultEncryptionKeyId())) {
            errors.add("Default encryption key ID is not among provided keys: " + hissProperties.getKeys().keySet());
        }
        if (!StringUtils.hasText(hissProperties.getDefaultEncryptionAlgorithm())) {
            errors.add("Default encryption algorithm is not defined");
        } else if (!encryptors.containsKey(hissProperties.getDefaultEncryptionAlgorithm())) {
            errors.add("Encryption algorithm " + hissProperties.getDefaultEncryptionAlgorithm() + " is not supported");
        }
    }

    private void validateDefaultHashingKeyAndAlgorithm(HissProperties hissProperties, ArrayList<String> errors) {
        if (!StringUtils.hasText(hissProperties.getDefaultHashingKeyId())) {
            errors.add("Default hashing key ID is not defined");
        }
        if (hissProperties.getKeys() != null
                && !hissProperties.getKeys().containsKey(hissProperties.getDefaultHashingKeyId())) {
            errors.add("Default hashing key ID is not among provided keys: " + hissProperties.getKeys().keySet());
        }
        if (!StringUtils.hasText(hissProperties.getDefaultHashingAlgorithm())) {
            errors.add("Default hashing algorithm is not defined");
        } else if (!hashers.containsKey(hissProperties.getDefaultHashingAlgorithm())) {
            errors.add("Hashing algorithm " + hissProperties.getDefaultHashingAlgorithm() + " is not supported");
        }
    }

}
