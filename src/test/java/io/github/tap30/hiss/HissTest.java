package io.github.tap30.hiss;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HissTest extends BaseHissTest {

    @Test
    void testPropertiesValidationWorks() {
        assertThrows(IllegalArgumentException.class, () ->
                HissFactory.createHiss(() ->
                        new HissProperties(null, null, null, null, null)));
    }

    @Test
    void testEncrypt() {
        // Given
        var text = "some text";

        // When
        var encrypted = hiss.encrypt(text);

        // Then
        assertNotEquals("some text", encrypted);
    }

    @Test
    void testEncrypt_whenContentIsAlreadyEncrypted() {
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
    void testEncrypt_whenValueIsNull() throws Exception {
        assertNull(hiss.encrypt(null));
    }

    @Test
    void testEncrypt_whenValueIsEmpty() throws Exception {
        assertEquals("", hiss.encrypt(""));
    }

    @Test
    void testDecrypt() {
        // Given
        var encryptedText = "#$$#{aes-128-gcm:default_key}{5Ki0pm8DwBRQPLXtkbBwNqAceuzgLEkOiZv6ecVNyPaAjqmme6gmVKw=}#$$#";

        // When
        var text = hiss.decrypt(encryptedText);

        // Then
        assertEquals("some text", text);
    }

    @Test
    void testDecrypt_whenContentNotEncrypted() {
        // Given
        var text = "some text";

        // When
        var decryptedText = hiss.decrypt(text);

        // Then
        assertEquals("some text", decryptedText);
    }

    @Test
    void testDecrypt_whenValueIsNull() throws Exception {
        assertNull(hiss.decrypt(null));
    }

    @Test
    void testDecrypt_whenValueIsEmpty() throws Exception {
        assertEquals("", hiss.decrypt(""));
    }

    @Test
    void testEncryptAndDecrypt() throws Exception {
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
    void testEncryptAndDecrypt_withWeiredValue01() {
        // Given
        var content = "$@$@N";

        // When
        var decryptedContent = hiss.decrypt(hiss.encrypt(content));

        // Then
        assertEquals(content, decryptedContent);
    }

    @Test
    void testEncryptAndDecrypt_withWeiredValue02() {
        // Given
        var content = "@MIRI";

        // When
        var decryptedContent = hiss.decrypt(hiss.encrypt(content));

        // Then
        assertEquals(content, decryptedContent);
    }

    @Test
    void testEncryptAndDecrypt_whenHavingPattern() throws Exception {
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
    void testHash() throws Exception {
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
    void testHash_whenValueIsNull() {
        assertNull(hiss.hash(null));
    }

    @Test
    void testHash_whenValueIsEmpty() {
        assertEquals("", hiss.hash(""));
    }

    @Test
    void testHash_producingSameValue() throws Exception {
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
    void testHash_whenHavingPattern() throws Exception {
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

    @Test
    void testEncryptAndDecryptDomainObject() throws Exception {
        // Given
        var aClassWithEncryptedAnnotationObject = new AClassWithEncryptedAnnotation();
        aClassWithEncryptedAnnotationObject.setNormalField("normal field");
        aClassWithEncryptedAnnotationObject.setField("field value");
        aClassWithEncryptedAnnotationObject.setHashedField("hashed field value");
        aClassWithEncryptedAnnotationObject.setCustomField("custom field value");
        aClassWithEncryptedAnnotationObject.setCustomHashedField("custom hashed field value");
        aClassWithEncryptedAnnotationObject.setFieldWithoutHash("field without hash");
        aClassWithEncryptedAnnotationObject.setFieldHavingPattern("Your code: 123456");
        aClassWithEncryptedAnnotationObject.setHashedFieldHavingPattern("hash of Your code: 123456");
        aClassWithEncryptedAnnotationObject.setParentField("parent field value");
        aClassWithEncryptedAnnotationObject.setHashedParentField("hashed parent field value");

        // When
        hiss.encryptObject(aClassWithEncryptedAnnotationObject);

        // Then
        assertEquals("normal field", aClassWithEncryptedAnnotationObject.getNormalField());

        assertNotEquals("field value", aClassWithEncryptedAnnotationObject.getField());
        assertNotEquals("hashed field value", aClassWithEncryptedAnnotationObject.getHashedField());
        assertEquals(hiss.hash("field value", ""), aClassWithEncryptedAnnotationObject.getHashedField());

        assertNotEquals("custom field value", aClassWithEncryptedAnnotationObject.getCustomField());
        assertNotEquals("custom hashed field value", aClassWithEncryptedAnnotationObject.getCustomHashedField());
        assertEquals(hiss.hash("custom field value", ""), aClassWithEncryptedAnnotationObject.getCustomHashedField());

        assertNotEquals("field without hash", aClassWithEncryptedAnnotationObject.getFieldWithoutHash());

        assertNotEquals("Your code: 123456", aClassWithEncryptedAnnotationObject.getFieldHavingPattern());
        assertTrue(aClassWithEncryptedAnnotationObject.getFieldHavingPattern().startsWith("Your code: "));
        assertNotEquals("hash of Your code: 123456", aClassWithEncryptedAnnotationObject.getHashedFieldHavingPattern());
        assertTrue(aClassWithEncryptedAnnotationObject.getHashedFieldHavingPattern().startsWith("Your code: "));
        assertTrue(aClassWithEncryptedAnnotationObject.getHashedFieldHavingPattern().contains(hiss.hash("123456", "")));

        assertNotEquals("parent field value", aClassWithEncryptedAnnotationObject.getParentField());
        assertNotEquals("hashed parent field value", aClassWithEncryptedAnnotationObject.getHashedParentField());

        assertNull(aClassWithEncryptedAnnotationObject.getNullField());
        assertNull(aClassWithEncryptedAnnotationObject.getHashedNullField());

        // When
        hiss.decryptObject(aClassWithEncryptedAnnotationObject);

        // Then
        assertEquals("normal field", aClassWithEncryptedAnnotationObject.getNormalField());
        assertEquals("field value", aClassWithEncryptedAnnotationObject.getField());
        assertNotEquals("field value", aClassWithEncryptedAnnotationObject.getHashedField());
        assertEquals("custom field value", aClassWithEncryptedAnnotationObject.getCustomField());
        assertNotEquals("field value", aClassWithEncryptedAnnotationObject.getCustomHashedField());
        assertEquals("field without hash", aClassWithEncryptedAnnotationObject.getFieldWithoutHash());
        assertEquals("Your code: 123456", aClassWithEncryptedAnnotationObject.getFieldHavingPattern());
        assertNotEquals("Your code: 123456", aClassWithEncryptedAnnotationObject.getHashedFieldHavingPattern());
        assertEquals("parent field value", aClassWithEncryptedAnnotationObject.getParentField());
        assertNull(aClassWithEncryptedAnnotationObject.getNullField());
        assertNull(aClassWithEncryptedAnnotationObject.getHashedNullField());
    }

    @Getter
    @Setter
    public static class ParentClassWithEncryptedAnnotation {
        @Encrypted
        private String parentField;
        private String hashedParentField;
    }

    @Getter
    @Setter
    public static class AClassWithEncryptedAnnotation extends ParentClassWithEncryptedAnnotation {
        private String normalField;
        @Encrypted
        private String field;
        private String hashedField;
        @Encrypted(hashFieldName = "customHashedField")
        private String customField;
        private String customHashedField;
        @Encrypted(hashingEnabled = false)
        private String fieldWithoutHash;
        @Encrypted(pattern = "\\d+")
        private String fieldHavingPattern;
        private String hashedFieldHavingPattern;
        @Encrypted
        private String nullField;
        private String hashedNullField;
    }

}