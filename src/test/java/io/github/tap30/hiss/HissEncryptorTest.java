package io.github.tap30.hiss;

import io.github.tap30.hiss.encryptor.Encryptor;
import io.github.tap30.hiss.key.Key;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class HissEncryptorTest {

    final String defaultAlgorithm = "default-alg";
    final String defaultKeyId = "default-key";
    final Key key = Key.builder().id(defaultKeyId).build();
    Encryptor encryptor;
    HissEncryptor hissEncryptor;

    @BeforeEach
    void setUpHissEncryptor() {
        encryptor = spy(new Encryptor() {
            @Override
            public byte[] encrypt(byte[] key, byte[] content) {
                return content;
            }

            @Override
            public byte[] decrypt(byte[] key, byte[] content) {
                return content;
            }

            @Override
            public String getName() {
                return defaultAlgorithm;
            }
        });

        hissEncryptor = new HissEncryptor(
                Map.of(defaultAlgorithm, encryptor),
                Map.of(defaultKeyId, key),
                defaultAlgorithm,
                defaultKeyId
        );
    }

    @Test
    void encrypt() throws Exception {
        // Given
        var text = "plain text";

        // When
        var encryptedText = hissEncryptor.encrypt(text, null);

        // Then
        assertEquals("#$$#{default-alg:default-key}{cGxhaW4gdGV4dA==}#$$#", encryptedText);
        verify(encryptor).encrypt(eq(key.getKey()), any());
    }

    @Test
    void encrypt_whenAlreadyEncrypted() throws Exception {
        // Given
        final var encryptedText = "#$$#{default-alg:default-key}{cGxhaW4gdGV4dA==}#$$#";

        // When
        var encryptedAgainText = hissEncryptor.encrypt(encryptedText, null);

        // Then
        assertEquals(encryptedText, encryptedAgainText);
        verify(encryptor, never()).encrypt(any(), any());
    }

    @Test
    void encrypt_whenContentIsNull() throws Exception {
        assertNull(hissEncryptor.encrypt(null, null));
    }

    @Test
    void encrypt_withPattern() throws Exception {
        // Given
        var text = "your secure code is 1234567890; keep it safe.";

        // When
        var encryptedText = hissEncryptor.encrypt(text, "\\d+");

        // Then
        assertEquals("your secure code is #$$#{default-alg:default-key}{MTIzNDU2Nzg5MA==}#$$#; keep it safe.",
                encryptedText);
    }

    @Test
    void decrypt() throws Exception {
        // Given
        var text = "#$$#{default-alg:default-key}{cGxhaW4gdGV4dA==}#$$#";

        // When
        var decryptedText = hissEncryptor.decrypt(text);

        // Then
        assertEquals("plain text", decryptedText);
        verify(encryptor).decrypt(eq(key.getKey()), any());
    }

    @Test
    void decrypt_whenIsNotEncrypted() throws Exception {
        // Given
        final var text = "plain text";

        // When
        var decryptedAgainText = hissEncryptor.decrypt(text);

        // Then
        assertEquals(text, decryptedAgainText);
        verify(encryptor, never()).decrypt(any(), any());
    }

    @Test
    void decrypt_whenContentIsNull() throws Exception {
        assertNull(hissEncryptor.decrypt(null));
    }

    @Test
    void decrypt_withPattern() throws Exception {
        // Given
        var text = "your secure code is #$$#{default-alg:default-key}{MTIzNDU2Nzg5MA==}#$$#; keep it safe.";

        // When
        var decryptedText = hissEncryptor.decrypt(text);

        // Then
        assertEquals("your secure code is 1234567890; keep it safe.",
                decryptedText);
    }

    @Test
    void encryptAndDecrypt() throws Exception {
        // Given
        final var text = "plain text";

        // When
        var encryptedText = hissEncryptor.encrypt(text, null);
        var decryptedText = hissEncryptor.decrypt(encryptedText);

        // Then
        assertEquals(text, decryptedText);
    }

    @Test
    void encryptAndDecrypt_withPattern() throws Exception {
        // Given
        final var text = "your secure code is 1234567890; keep it safe.";

        // When
        var encryptedText = hissEncryptor.encrypt(text, "\\d+");
        var decryptedText = hissEncryptor.decrypt(encryptedText);

        // Then
        assertEquals(text, decryptedText);
    }

    @Test
    void isEncrypted() throws Exception {
        // Given
        var text = "plain text";
        var encryptedText = hissEncryptor.encrypt(text, null);

        // When & Then
        assertFalse(hissEncryptor.isEncrypted(text));
        assertTrue(hissEncryptor.isEncrypted(encryptedText));
    }

    @Test
    void isEncrypted_whenHavingPattern() throws Exception {
        // Given
        var text = "your secure code is 1234567890; keep it safe.";
        var encryptedText = hissEncryptor.encrypt(text, "\\d+");

        // When & Then
        assertFalse(hissEncryptor.isEncrypted(text));
        assertTrue(hissEncryptor.isEncrypted(encryptedText));
    }

}