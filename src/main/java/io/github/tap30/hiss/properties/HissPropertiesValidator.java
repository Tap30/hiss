package io.github.tap30.hiss.properties;

import io.github.tap30.hiss.key.KeyHashGenerator;
import io.github.tap30.hiss.utils.StringUtils;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class HissPropertiesValidator {

    private static final Set<String> SUPPORTED_ENCRYPTION_ALGORITHMS =
            Set.of("aes-128-gcm", "aes-128-cbc"); // Todo: get these from algorithm spec
    private static final Set<String> SUPPORTED_HASHING_ALGORITHMS =
            Set.of("hmac-sha256"); // Todo: get this from algorithm spec

    private final KeyHashGenerator keyHashGenerator;

    public HissPropertiesValidator(KeyHashGenerator keyHashGenerator) {
        this.keyHashGenerator = Objects.requireNonNull(keyHashGenerator);
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

    private static void validateDefaultEncryptionKeyAndAlgorithm(HissProperties hissProperties, ArrayList<String> errors) {
        if (!StringUtils.hasText(hissProperties.getDefaultEncryptionKeyId())) {
            errors.add("Default encryption key ID is not defined");
        }
        if (!hissProperties.getKeys().containsKey(hissProperties.getDefaultEncryptionKeyId())) {
            errors.add("Default encryption key ID is not among provided keys: " + hissProperties.getKeys().keySet());
        }
        if (!StringUtils.hasText(hissProperties.getDefaultEncryptionAlgorithm())) {
            errors.add("Default encryption algorithm is not defined");
        } else if (!SUPPORTED_ENCRYPTION_ALGORITHMS.contains(hissProperties.getDefaultEncryptionAlgorithm())) {
            errors.add("Encryption algorithm " + hissProperties.getDefaultEncryptionAlgorithm() + " is not supported");
        }
    }

    private static void validateDefaultHashingKeyAndAlgorithm(HissProperties hissProperties, ArrayList<String> errors) {
        if (!StringUtils.hasText(hissProperties.getDefaultHashingKeyId())) {
            errors.add("Default hashing key ID is not defined");
        }
        if (!hissProperties.getKeys().containsKey(hissProperties.getDefaultHashingKeyId())) {
            errors.add("Default hashing key ID is not among provided keys: " + hissProperties.getKeys().keySet());
        }
        if (!StringUtils.hasText(hissProperties.getDefaultHashingAlgorithm())) {
            errors.add("Default hashing algorithm is not defined");
        } else if (!SUPPORTED_HASHING_ALGORITHMS.contains(hissProperties.getDefaultHashingAlgorithm())) {
            errors.add("Hashing algorithm " + hissProperties.getDefaultHashingAlgorithm() + " is not supported");
        }
    }

}
