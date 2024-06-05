package io.github.tap30.hiss;

import lombok.Value;
import org.intellij.lang.annotations.Language;
import io.github.tap30.hiss.utils.ReflectionUtils;
import io.github.tap30.hiss.utils.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

class HissObjectEncryptor {

    private static final Logger logger = Logger.getLogger(HissObjectEncryptor.class.getName());
    private final static Map<Class<?>, ClassDescription> CLASSES_DESCRIPTION_CACHE = new HashMap<>();

    private final HissEncryptor hissEncryptor;


    public HissObjectEncryptor(HissEncryptor hissEncryptor) {
        this.hissEncryptor = hissEncryptor;
    }

    public void encryptObject(Object domainObject) {
        this.encryptFields(domainObject);
    }

    public void decryptObject(Object domainObject) {
        this.decryptFields(domainObject);
    }

    void encryptFields(Object object) {
        this.processFields(object, this::encryptField);
    }

    void decryptFields(Object object) {
        this.processFields(object, this::decryptField);
    }

    private void processFields(Object object,
                               BiConsumer<FieldAnnotatedWithEncrypted, Object> processor) {
        if (object == null) return;

        var classDescription = getClassDescription(object.getClass());
        for (var field : classDescription.getFieldsAnnotatedWithEncrypted()) {
            processor.accept(field, object);
        }
        for (var field : classDescription.getFieldsAnnotatedWithEncryptedInside()) {
            this.processFieldsAnnotatedWithEncryptedInside(field, object, processor);
        }
    }

    private void processFieldsAnnotatedWithEncryptedInside(FieldAnnotatedWithEncryptedInside field,
                                                           Object object,
                                                           BiConsumer<FieldAnnotatedWithEncrypted, Object> processor) {
        var fieldContent = ReflectionUtils.invokeGetter(field.getGetter(), object, Object.class);
        if (fieldContent instanceof Iterable<?>) {
            ((Iterable<?>) fieldContent).forEach(item -> this.processFields(item, processor));
        } else if (fieldContent instanceof Map<?, ?>) {
            ((Map<?, ?>) fieldContent).forEach((k, v) -> this.processFields(v, processor));
        } else {
            this.processFields(fieldContent, processor);
        }
    }

    private void encryptField(FieldAnnotatedWithEncrypted fieldAnnotatedWithEncrypted, Object object) {
        try {
            var content = getContent(fieldAnnotatedWithEncrypted, object);
            if (content == null) {
                return;
            }
            @Language("regexp")
            var pattern = fieldAnnotatedWithEncrypted.getEncryptedAnnotation().pattern();
            var encryptedContent = this.hissEncryptor.encrypt(content, pattern);
            fieldAnnotatedWithEncrypted.getContentField().getSetter().invoke(object, encryptedContent);
            if (fieldAnnotatedWithEncrypted.getEncryptedAnnotation().hashingEnabled()) {
                var hashedContent = this.hissEncryptor.hash(content, pattern);
                fieldAnnotatedWithEncrypted.getHashField().getSetter().invoke(object, hashedContent);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void decryptField(FieldAnnotatedWithEncrypted fieldAnnotatedWithEncrypted, Object object) {
        try {
            var content = getContent(fieldAnnotatedWithEncrypted, object);
            var decryptedContent = this.hissEncryptor.decrypt(content);
            fieldAnnotatedWithEncrypted.getContentField().getSetter().invoke(object, decryptedContent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ClassDescription getClassDescription(Class<?> clazz) {
        if (CLASSES_DESCRIPTION_CACHE.containsKey(clazz)) {
            return CLASSES_DESCRIPTION_CACHE.get(clazz);
        }

        var fieldsAnnotatedWithEncrypted = new ArrayList<FieldAnnotatedWithEncrypted>();
        var fieldsAnnotatedWithEncryptedInside = new ArrayList<FieldAnnotatedWithEncryptedInside>();
        for (var field : ReflectionUtils.getAllFields(clazz)) {
            getFieldAnnotatedWithEncrypted(field, clazz).ifPresent(fieldsAnnotatedWithEncrypted::add);
            getFieldAnnotatedWithEncryptedInside(field, clazz).ifPresent(fieldsAnnotatedWithEncryptedInside::add);
        }

        var classDescription = new ClassDescription(fieldsAnnotatedWithEncrypted, fieldsAnnotatedWithEncryptedInside);
        CLASSES_DESCRIPTION_CACHE.put(clazz, classDescription);
        if (CLASSES_DESCRIPTION_CACHE.size() > 1000) {
            logger.log(Level.WARNING, "{0} classes are cached", CLASSES_DESCRIPTION_CACHE.size());
        }
        return classDescription;
    }

    private static Optional<FieldAnnotatedWithEncrypted>
    getFieldAnnotatedWithEncrypted(Field field, Class<?> clazz) {
        var encryptedAnnotation = field.getDeclaredAnnotation(Encrypted.class);
        if (encryptedAnnotation != null) {
            var contentField = getDataField(field, clazz);
            var hashField = getHashField(clazz, field, encryptedAnnotation);
            return Optional.of(new FieldAnnotatedWithEncrypted(encryptedAnnotation, contentField, hashField));
        } else {
            return Optional.empty();
        }
    }

    private static Optional<FieldAnnotatedWithEncryptedInside>
    getFieldAnnotatedWithEncryptedInside(Field field, Class<?> clazz) {
        var encryptedInsideAnnotation = field.getDeclaredAnnotation(EncryptedInside.class);
        if (encryptedInsideAnnotation != null) {
            return Optional.of(new FieldAnnotatedWithEncryptedInside(field, getGetter(field, clazz)));
        } else {
            return Optional.empty();
        }
    }

    private static DataField getHashField(Class<?> clazz, Field field, Encrypted encryptedAnnotation) {
        DataField hashField = null;
        if (encryptedAnnotation.hashingEnabled()) {
            if (StringUtils.hasText(encryptedAnnotation.hashFieldName())) {
                hashField = getDataField(encryptedAnnotation.hashFieldName(), clazz);
            } else {
                var fieldName = "hashed" + capitalizeFirstLetter(field.getName());
                hashField = getDataField(fieldName, clazz);
            }
        }
        return hashField;
    }

    private static DataField getDataField(String fieldName, Class<?> clazz) {
        return getDataField(getField(fieldName, clazz), clazz);
    }

    private static Field getField(String fieldName, Class<?> clazz) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return getField(fieldName, clazz.getSuperclass());
            }
            throw new RuntimeException(e);
        }
    }

    private static String getContent(FieldAnnotatedWithEncrypted fieldAnnotatedWithEncrypted, Object object) throws IllegalAccessException, InvocationTargetException {
        return ReflectionUtils.invokeGetter(fieldAnnotatedWithEncrypted.getContentField().getGetter(), object, String.class);
    }

    private static DataField getDataField(Field field, Class<?> clazz) {
        try {
            var firstLetterCapitalizedFieldName = capitalizeFirstLetter(field.getName());
            var getter = clazz.getDeclaredMethod("get" + firstLetterCapitalizedFieldName);
            var setter = clazz.getDeclaredMethod("set" + firstLetterCapitalizedFieldName, String.class);
            return new DataField(field, getter, setter);
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null) {
                return getDataField(field, clazz.getSuperclass());
            }
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Method getGetter(Field field, Class<?> clazz) {
        try {
            var firstLetterCapitalizedFieldName = capitalizeFirstLetter(field.getName());
            return clazz.getDeclaredMethod("get" + firstLetterCapitalizedFieldName);
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null) {
                return getGetter(field, clazz.getSuperclass());
            }
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String capitalizeFirstLetter(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    @Value
    private static class ClassDescription {
        List<FieldAnnotatedWithEncrypted> fieldsAnnotatedWithEncrypted;
        List<FieldAnnotatedWithEncryptedInside> fieldsAnnotatedWithEncryptedInside;
    }

    @Value
    private static class FieldAnnotatedWithEncryptedInside {
        Field field;
        Method getter;
    }

    @Value
    private static class FieldAnnotatedWithEncrypted {
        Encrypted encryptedAnnotation;
        DataField contentField;
        DataField hashField;
    }

    @Value
    private static class DataField {
        Field field;
        Method getter;
        Method setter;
    }

}
