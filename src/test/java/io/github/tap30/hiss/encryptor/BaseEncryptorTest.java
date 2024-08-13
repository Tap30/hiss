package io.github.tap30.hiss.encryptor;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public abstract class BaseEncryptorTest {

    protected final Encryptor encryptor;
    protected final String encryptorName;
    protected final byte[] key;

    protected final String plainText = "some text";
    protected final byte[] plainTextBytes = plainText.getBytes();

    protected final String encodedEncryptedText;
    protected final byte[] encryptedTextBytes;

    protected BaseEncryptorTest(Encryptor encryptor,
                                String encryptorName,
                                byte[] key,
                                String encodedEncryptedText) {
        this.encryptor = encryptor;
        this.encryptorName = encryptorName;
        this.key = key;
        this.encodedEncryptedText = encodedEncryptedText;
        this.encryptedTextBytes = base64(encodedEncryptedText);
    }

    protected String base64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    protected byte[] base64(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    @Test
    void testEncrypt() throws Exception {
        // When
        var encrypted = encryptor.encrypt(key, plainTextBytes);

        // Then
        assertNotEquals(plainTextBytes, encrypted);
        System.out.printf("Base64 Encoded Encrypted Content: %s\n", base64(encrypted));
    }

    @Test
    void testDecrypt() throws Exception {
        // When
        var plain = encryptor.decrypt(key, encryptedTextBytes);

        // Then
        assertArrayEquals(plainTextBytes, plain);
    }

    @Test
    void testEncryptAndDecrypt() throws Exception {
        // When
        var encrypted = encryptor.encrypt(key, plainTextBytes);
        var plain = encryptor.decrypt(key, encrypted);

        // Then
        assertArrayEquals(plainTextBytes, plain);
    }

    @Test
    void testGetName() {
        assertEquals(encryptorName, encryptor.getName());
    }

}