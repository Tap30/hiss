package io.github.tap30.hiss.properties;

import io.github.tap30.hiss.utils.StringUtils;

import java.util.ArrayList;

public class HissPropertiesValidator {

    public void validate(HissProperties hissProperties) {
        var errors = new ArrayList<String>();
        if (hissProperties.getKeys() == null || hissProperties.getKeys().isEmpty()) {
            errors.add("Keys are empty");
        } else {
            hissProperties.getKeys().forEach((k, v) -> {
                if (v == null || v.length == 0) {
                    errors.add("Key " + k + " is empty");
                }
            });
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
