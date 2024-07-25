package io.github.tap30.hiss.properties;

import io.github.tap30.hiss.key.KeyHashGenerator;
import io.github.tap30.hiss.utils.StringUtils;

import java.util.ArrayList;

public class HissPropertiesValidator {

    private final KeyHashGenerator keyHashGenerator;

    public HissPropertiesValidator(KeyHashGenerator keyHashGenerator) {
        this.keyHashGenerator = keyHashGenerator;
    }

    public void validate(HissProperties hissProperties) {
        var errors = new ArrayList<String>();
        if (hissProperties.getKeys() == null || hissProperties.getKeys().isEmpty()) {
            errors.add("Keys are empty");
        } else {
            hissProperties.getKeys().forEach((k, v) -> {
                if (v == null || v.getKey() == null || v.getKey().length == 0) {
                    errors.add("Key " + k + " is empty");
                }
            });
            var mismatches = keyHashGenerator.validateKeyHashes(hissProperties.getKeys().values());
            if (!mismatches.isEmpty()) {
                errors.add("Key(s) " + mismatches + " did not match with their hashes");
            }
        }
        if (!StringUtils.hasText(hissProperties.getDefaultEncryptionKeyId())) {
            errors.add("Default encryption key ID is not defined");
        }
        if (!StringUtils.hasText(hissProperties.getDefaultEncryptionAlgorithm())) {
            errors.add("Default encryption algorithm is not defined");
        }
        if (!StringUtils.hasText(hissProperties.getDefaultHashingKeyId())) {
            errors.add("Default hashing key ID is not defined");
        }
        if (!StringUtils.hasText(hissProperties.getDefaultHashingAlgorithm())) {
            errors.add("Default hashing algorithm is not defined");
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Hiss properties are not valid: " + String.join("; ", errors));
        }
    }

}
