package io.github.tap30.hiss.properties;

import io.github.tap30.hiss.encryptor.Encryptor;
import io.github.tap30.hiss.hasher.Hasher;
import io.github.tap30.hiss.key.Key;
import io.github.tap30.hiss.key.KeyHashGenerator;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class HissPropertiesValidatorTest {

    KeyHashGenerator keyHashGenerator = mock(KeyHashGenerator.class);
    HissPropertiesValidator hissPropertiesValidator = new HissPropertiesValidator(
            keyHashGenerator,
            Map.of("aes-128-gcm", mock(Encryptor.class)),
            Map.of("hmac-sha256", mock(Hasher.class))
    );

    @Test
    void testConstructor_whenConstructionArgumentsAreNull() {
        assertThrows(NullPointerException.class,
                () -> new HissPropertiesValidator(null, Map.of(), Map.of()));
        assertThrows(NullPointerException.class,
                () -> new HissPropertiesValidator(keyHashGenerator, null, Map.of()));
        assertThrows(NullPointerException.class,
                () -> new HissPropertiesValidator(keyHashGenerator, Map.of(), null));
    }

    // Keys Validation

    @Test
    void testValidate_whenPropertiesAreValid() {
        // Given
        var properties = createValidProperties();

        // When & Then
        assertDoesNotThrow(() -> hissPropertiesValidator.validate(properties));
    }

    @Test
    void testValidate_whenKeysAreEmpty() {
        // Given
        var properties = spy(createValidProperties());
        doReturn(Map.of()).when(properties).getKeys();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> hissPropertiesValidator.validate(properties));
    }

    @Test
    void testValidate_whenKeysHaveAKeyWithoutName() {
        // Given
        var properties = spy(createValidProperties());
        var keys = new HashMap<String, Key>();
        keys.put(null, Key.builder().build());
        doReturn(keys).when(properties).getKeys();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> hissPropertiesValidator.validate(properties));
    }

    @Test
    void testValidate_whenKeyBytesAreEmpty() {
        // Given
        var properties = spy(createValidProperties());
        doReturn(Map.of("default_key", Key.builder().build())).when(properties).getKeys();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> hissPropertiesValidator.validate(properties));
    }

    @Test
    void testValidate_whenKeyHashIsNotCorrect() {
        // Given
        var properties = createValidProperties();
        doReturn(Set.of("default_key")).when(keyHashGenerator).validateKeyHashes(any());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> hissPropertiesValidator.validate(properties));
    }

    // Default Encryption Key and Algorithm Validation

    @Test
    void testValidate_whenDefaultEncryptionKeyIdIsMissing() {
        // Given
        var properties = spy(createValidProperties());
        doReturn(null).when(properties).getDefaultEncryptionKeyId();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> hissPropertiesValidator.validate(properties));
    }

    @Test
    void testValidate_whenDefaultEncryptionKeyIdIsInvalid() {
        // Given
        var properties = spy(createValidProperties());
        doReturn("some unknown key").when(properties).getDefaultEncryptionKeyId();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> hissPropertiesValidator.validate(properties));
    }

    @Test
    void testValidate_whenDefaultEncryptionAlgorithmIsMissing() {
        // Given
        var properties = spy(createValidProperties());
        doReturn(null).when(properties).getDefaultEncryptionAlgorithm();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> hissPropertiesValidator.validate(properties));
    }

    @Test
    void testValidate_whenDefaultEncryptionAlgorithmIsInvalid() {
        // Given
        var properties = spy(createValidProperties());
        doReturn("some unknown algorithm").when(properties).getDefaultEncryptionAlgorithm();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> hissPropertiesValidator.validate(properties));
    }

    // Default Hashing Key and Algorithm Validation

    @Test
    void testValidate_whenDefaultHashingKeyIdIsMissing() {
        // Given
        var properties = spy(createValidProperties());
        doReturn(null).when(properties).getDefaultHashingKeyId();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> hissPropertiesValidator.validate(properties));
    }

    @Test
    void testValidate_whenDefaultHashingKeyIdIsInvalid() {
        // Given
        var properties = spy(createValidProperties());
        doReturn("some unknown key").when(properties).getDefaultHashingKeyId();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> hissPropertiesValidator.validate(properties));
    }

    @Test
    void testValidate_whenDefaultHashingAlgorithmIsMissing() {
        // Given
        var properties = spy(createValidProperties());
        doReturn(null).when(properties).getDefaultHashingAlgorithm();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> hissPropertiesValidator.validate(properties));
    }

    @Test
    void testValidate_whenDefaultHashingAlgorithmIsInvalid() {
        // Given
        var properties = spy(createValidProperties());
        doReturn("some unknown algorithm").when(properties).getDefaultHashingAlgorithm();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> hissPropertiesValidator.validate(properties));
    }

    HissProperties createValidProperties() {
        return new HissProperties() {
            @Override
            protected Set<Key> loadKeys() {
                return Set.of(Key.builder()
                        .id("default_key")
                        .key(Base64.getDecoder().decode("AAAAAAAAAAAAAAAAAAAAAA=="))
                        .keyHash("$2a$12$3T0VMnGMgvesehYomommnO02dbFOJuM/3elsmgmsB2/qlGSF3BIbe")
                        .build());
            }

            @Override
            protected String loadDefaultEncryptionKeyId() {
                return "default_key";
            }

            @Override
            protected String loadDefaultEncryptionAlgorithm() {
                return "aes-128-gcm";
            }

            @Override
            protected String loadDefaultHashingKeyId() {
                return "default_key";
            }

            @Override
            protected String loadDefaultHashingAlgorithm() {
                return "hmac-sha256";
            }

            @Override
            protected boolean loadKeyHashGenerationEnabled() {
                return true;
            }
        };
    }

}