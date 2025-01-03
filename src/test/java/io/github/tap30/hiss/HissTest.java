package io.github.tap30.hiss;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HissTest extends BaseHissTest {


    @Test
    void encrypt() {
        // Given
        var text = "some text";

        // When
        var encrypted = hiss.encrypt(text);

        // Then
        assertNotEquals("some text", encrypted);
    }

    @Test
    void encrypt_whenContentIsAlreadyEncrypted() {
        // Given
        var text = "some text";

        // When
        var encrypted = hiss.encrypt(text);
        encrypted = hiss.encrypt(encrypted);
        encrypted = hiss.encrypt(encrypted);

        // Then
        assertEquals("some text", hiss.decrypt(encrypted));
    }

    @Test
    void encrypt_whenValueIsNull() {
        assertNull(hiss.encrypt(null));
    }

    @Test
    void encrypt_whenValueIsEmpty() {
        assertEquals("", hiss.encrypt(""));
    }

    @Test
    void decrypt() {
        // Given
        var encryptedText = "#$$#{aes-128-gcm:default_key}{5Ki0pm8DwBRQPLXtkbBwNqAceuzgLEkOiZv6ecVNyPaAjqmme6gmVKw=}#$$#";

        // When
        var text = hiss.decrypt(encryptedText);

        // Then
        assertEquals("some text", text);
    }

    @Test
    void decrypt_whenContentNotEncrypted() {
        // Given
        var text = "some text";

        // When
        var decryptedText = hiss.decrypt(text);

        // Then
        assertEquals("some text", decryptedText);
    }

    @Test
    void decrypt_whenValueIsNull() {
        assertNull(hiss.decrypt(null));
    }

    @Test
    void decrypt_whenValueIsEmpty() {
        assertEquals("", hiss.decrypt(""));
    }

    @Test
    void encryptAndDecrypt() {
        // Given
        final var content = "Hello; user with phone number +989123456789 and national code 1234567890 is verified.";

        // When
        var encryptedContent = hiss.encrypt(content);
        var decryptedContent = hiss.decrypt(encryptedContent);

        // Then
        assertFalse(hiss.isEncrypted(content));
        assertTrue(hiss.isEncrypted(encryptedContent));
        assertNotEquals(content, encryptedContent);
        assertFalse(encryptedContent.contains("+989123456789"));
        assertFalse(encryptedContent.contains("1234567890"));
        assertTrue(encryptedContent.matches("#\\$\\$#\\{aes-128-gcm:default_key}\\{.+?}#\\$\\$#"));
        assertEquals(content, decryptedContent);
    }

    @Test
    void encryptAndDecrypt_withWeiredValue01() {
        // Given
        var content = "$@$@N";

        // When
        var decryptedContent = hiss.decrypt(hiss.encrypt(content));

        // Then
        assertEquals(content, decryptedContent);
    }

    @Test
    void encryptAndDecrypt_withWeiredValue02() {
        // Given
        var content = "@MIRI";

        // When
        var decryptedContent = hiss.decrypt(hiss.encrypt(content));

        // Then
        assertEquals(content, decryptedContent);
    }

    @Test
    void encryptAndDecrypt_whenHavingPattern() {
        // Given
        final var content = "Hello; user with phone number +989123456789 and national code 1234567890 is verified.";

        // When
        var encryptedContent = hiss.encrypt(content, "\\+989\\d+|\\d{10}");
        var decryptedContent = hiss.decrypt(encryptedContent);

        // Then
        assertFalse(hiss.isEncrypted(content));
        assertTrue(hiss.isEncrypted(encryptedContent));
        assertNotEquals(content, encryptedContent);
        assertFalse(encryptedContent.contains("+989123456789"));
        assertFalse(encryptedContent.contains("1234567890"));
        assertTrue(encryptedContent.matches("Hello; user with phone number " +
                                            "#\\$\\$#\\{aes-128-gcm:default_key}\\{.+?}#\\$\\$#" +
                                            " and national code " +
                                            "#\\$\\$#\\{aes-128-gcm:default_key}\\{.+?}#\\$\\$#" +
                                            " is verified."));
        assertEquals(content, decryptedContent);
    }

    @Test
    void hash() {
        // Given
        final var content = "Hello; user with phone number +989123456789 and national code 1234567890 is verified.";

        // When
        var hashedContent = hiss.hash(content);

        // Then
        assertFalse(hiss.isHashed(content));
        assertTrue(hiss.isHashed(hashedContent));
        assertNotEquals(content, hashedContent);
        assertFalse(hashedContent.contains("+989123456789"));
        assertFalse(hashedContent.contains("1234567890"));
        assertTrue(hashedContent.matches("#\\$\\$#\\{hmac-sha256:default_key}\\{.+?}#\\$\\$#"));
    }

    @Test
    void hash_whenValueIsNull() {
        assertNull(hiss.hash(null));
    }

    @Test
    void hash_whenValueIsEmpty() {
        assertEquals("", hiss.hash(""));
    }

    @Test
    void hash_producingSameValue() {
        // Given
        final var content = "Hello; user with phone number +989123456789 and national code 1234567890 is verified.";

        // When
        var hashedContent1 = hiss.hash(content, "");
        var hashedContent2 = hiss.hash(content, "");
        var hashedContent3 = hiss.hash(content, "");

        // Then
        assertEquals(hashedContent1, hashedContent2);
        assertEquals(hashedContent1, hashedContent3);
    }

    @Test
    void hash_whenHavingPattern() {
        // Given
        final var content = "Hello; user with phone number +989123456789 and national code 1234567890 is verified.";

        // When
        var hashedContent = hiss.hash(content, "\\+989\\d+|\\d{10}");

        // Then
        assertFalse(hiss.isHashed(content));
        assertTrue(hiss.isHashed(hashedContent));
        assertNotEquals(content, hashedContent);
        assertFalse(hashedContent.contains("+989123456789"));
        assertFalse(hashedContent.contains("1234567890"));
        assertTrue(hashedContent.matches("Hello; user with phone number " +
                                         "#\\$\\$#\\{hmac-sha256:default_key}\\{.+?}#\\$\\$#" +
                                         " and national code " +
                                         "#\\$\\$#\\{hmac-sha256:default_key}\\{.+?}#\\$\\$#" +
                                         " is verified."));
    }

}