package io.github.tap30.hiss.key;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class KeyHashGeneratorTest {

    BCrypt.Hasher hasher = BCrypt.withDefaults();
    BCrypt.Verifyer verifier = BCrypt.verifyer();
    KeyHashGenerator keyHashGenerator = new KeyHashGenerator(hasher, verifier);

    @Test
    void testGenerateHashes() {
        // Given
        var keys = Set.of(
                Key.builder().id("key1").key(new byte[]{1, 2, 3}).build(),
                Key.builder().id("key2").key(new byte[]{4, 5, 6}).build()
        );

        // When
        var hashes = keyHashGenerator.generateHashes(keys);
        System.out.println(hashes);

        // Then
        assertEquals(2, hashes.size());
        assertTrue(verifier.verify(new byte[]{1, 2, 3}, hashes.get("key1").getBytes(StandardCharsets.UTF_8)).verified);
        assertTrue(verifier.verify(new byte[]{4, 5, 6}, hashes.get("key2").getBytes(StandardCharsets.UTF_8)).verified);
    }

    @Test
    void testValidateKeyHashes() {
        // Given
        var keys = Set.of(
                Key.builder()
                        .id("key1")
                        .key(new byte[]{1, 2, 3})
                        .keyHash("$2a$12$tvVEa2yZ/RhSbYg16GG5hO5a/2P9HWVWM8c8ISZpgLIRvlF3EzVgm")
                        .build(),
                Key.builder()
                        .id("key2")
                        .key(new byte[]{4, 5, 6})
                        .keyHash("$2a$12$fwlilo5GtK44245Xcg57HuvNDEhJM7snmQ7VOO2LQfGvtvOk8tbpS")
                        .build()
        );

        // When
        var invalidKeys = keyHashGenerator.validateKeyHashes(keys);

        // Then
        assertEquals(0, invalidKeys.size());
    }

    @Test
    void testValidateKeyHashes_whenKeyHashesAreInvalid() {
        // Given
        var keys = Set.of(
                Key.builder()
                        .id("key1")
                        .key(new byte[]{1, 2, 3})
                        .keyHash("chert1")
                        .build(),
                Key.builder()
                        .id("key2")
                        .key(new byte[]{4, 5, 6})
                        .keyHash("chert2")
                        .build()
        );

        // When
        var invalidKeys = keyHashGenerator.validateKeyHashes(keys);

        // Then
        assertEquals(2, invalidKeys.size());
        assertTrue(invalidKeys.contains("key1"));
        assertTrue(invalidKeys.contains("key2"));
    }

    @Test
    void testGenerateAndLogAllHashes() {
        // Given
        var keys = Set.<Key>of();
        var keyHashGenerator = spy(this.keyHashGenerator);

        // When
        keyHashGenerator.generateAndLogHashes(keys);

        // Then
        verify(keyHashGenerator).generateHashes(keys);
    }

}