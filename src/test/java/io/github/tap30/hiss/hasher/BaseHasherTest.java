package io.github.tap30.hiss.hasher;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BaseHasherTest {

    protected final Hasher hasher;
    protected final String hasherName;
    protected final byte[] key;

    protected final String plainText = "some text";
    protected final byte[] plainTextBytes = plainText.getBytes();

    protected final String encodedHashedText;
    protected final byte[] hashedTextBytes;

    protected BaseHasherTest(Hasher hasher, String hasherName, byte[] key, String encodedHashedText) {
        this.hasher = hasher;
        this.hasherName = hasherName;
        this.key = key;
        this.encodedHashedText = encodedHashedText;
        this.hashedTextBytes = base64(encodedHashedText);
    }

    protected String base64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    protected byte[] base64(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    @Test
    void testHash() throws Exception {
        // When
        var hash = hasher.hash(key, plainTextBytes);

        // Then
        System.out.println(base64(hash));
        assertArrayEquals(hashedTextBytes, hash);
    }

    @Test
    void testHash_producesSameHashForSameInput() throws Exception {
        // When
        var hash1 = hasher.hash(key, plainTextBytes);
        var hash2 = hasher.hash(key, plainTextBytes);
        var hash3 = hasher.hash(key, plainTextBytes);

        // Then
        assertArrayEquals(hashedTextBytes, hash1);
        assertArrayEquals(hashedTextBytes, hash2);
        assertArrayEquals(hashedTextBytes, hash3);
    }

    @Test
    void testGetName() {
        assertEquals(hasherName, hasher.getName());
    }

}