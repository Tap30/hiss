package io.github.tap30.hiss;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptedAnnotationTest extends BaseHissTest {

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
