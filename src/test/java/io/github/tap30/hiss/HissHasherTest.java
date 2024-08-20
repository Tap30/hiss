package io.github.tap30.hiss;

import io.github.tap30.hiss.hasher.Hasher;
import io.github.tap30.hiss.key.Key;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class HissHasherTest {

    final String defaultAlgorithm = "default-alg";
    final String defaultKeyId = "default-key";
    final Key key = Key.builder().id(defaultKeyId).build();
    Hasher hasher;
    HissHasher hissHasher;

    @BeforeEach
    void setUpHissHasher() {
        hasher = spy(new Hasher() {
            @Override
            public byte[] hash(byte[] key, byte[] content) {
                return content;
            }

            @Override
            public String getName() {
                return defaultAlgorithm;
            }
        });

        hissHasher = new HissHasher(
                Map.of(defaultAlgorithm, hasher),
                Map.of(defaultKeyId, key),
                defaultAlgorithm,
                defaultKeyId
        );
    }

    @Test
    void hash() throws Exception {
        // Given
        var text = "plain text";

        // When
        var hashedText = hissHasher.hash(text, null);

        // Then
        assertEquals("#$$#{default-alg:default-key}{cGxhaW4gdGV4dA==}#$$#", hashedText);
        verify(hasher).hash(eq(key.getKey()), any());
    }

    @Test
    void hash_whenAlreadyHashed() throws Exception {
        // Given
        final var hashedText = "#$$#{default-alg:default-key}{cGxhaW4gdGV4dA==}#$$#";

        // When
        var hashedAgainText = hissHasher.hash(hashedText, null);

        // Then
        assertEquals(hashedText, hashedAgainText);
        verify(hasher, never()).hash(any(), any());
    }

    @Test
    void hash_whenContentIsNull() throws Exception {
        assertNull(hissHasher.hash(null, null));
    }

    @Test
    void hash_withPattern() throws Exception {
        // Given
        var text = "your secure code is 1234567890; keep it safe.";

        // When
        var hashedText = hissHasher.hash(text, "\\d+");

        // Then
        assertEquals("your secure code is #$$#{default-alg:default-key}{MTIzNDU2Nzg5MA==}#$$#; keep it safe.",
                hashedText);
    }


    @Test
    void isHashed() throws Exception {
        // Given
        var text = "plain text";
        var encryptedText = hissHasher.hash(text, null);

        // When & Then
        assertFalse(hissHasher.isHashed(text));
        assertTrue(hissHasher.isHashed(encryptedText));
    }

    @Test
    void isHashed_whenHavingPattern() throws Exception {
        // Given
        var text = "your secure code is 1234567890; keep it safe.";
        var encryptedText = hissHasher.hash(text, "\\d+");

        // When & Then
        assertFalse(hissHasher.isHashed(text));
        assertTrue(hissHasher.isHashed(encryptedText));
    }

}