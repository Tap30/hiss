package io.github.tap30.hiss;

import io.github.tap30.hiss.utils.ReflectionUtils;
import io.github.tap30.hiss.utils.StringUtils;
import lombok.Value;
import org.intellij.lang.annotations.Language;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

class HissObjectEncryptor {

    private static final Logger logger = Logger.getLogger(HissObjectEncryptor.class.getName());
    private static final Map<Class<?>, ClassDescription> CLASSES_DESCRIPTION_CACHE = new HashMap<>();

    private final HissEncryptor hissEncryptor;
    private final HissHasher hissHasher;

    public HissObjectEncryptor(HissEncryptor hissEncryptor,
                               HissHasher hissHasher) {
        this.hissEncryptor = Objects.requireNonNull(hissEncryptor);
        this.hissHasher = Objects.requireNonNull(hissHasher);
    }

    public void encryptObject(Object domainObject) {
        this.encryptFields(domainObject);
    }

    public void decryptObject(Object domainObject) {
        this.decryptFields(domainObject);
    }

    private void encryptFields(Object object) {
        this.processFields(object, this::encryptField);
    }

    private void decryptFields(Object object) {
        this.processFields(object, this::decryptField);
    }

    private void processFields(Object object,
                               BiConsumer<Object, FieldAnnotatedWithEncrypted> processor) {
        if (object == null) return;

        var classDescription = getClassDescription(object.getClass());
        for (var field : classDescription.getFieldsAnnotatedWithEncrypted()) {
            processor.accept(object, field);
        }
        for (var field : classDescription.getFieldsAnnotatedWithEncryptedInside()) {
            this.processFieldsAnnotatedWithEncryptedInside(object, field, processor);
        }
    }

    private void processFieldsAnnotatedWithEncryptedInside(Object object,
                                                           FieldAnnotatedWithEncryptedInside fieldAnnotatedWithEncryptedInside,
                                                           BiConsumer<Object, FieldAnnotatedWithEncrypted> processor) {
        var fieldContent = fieldAnnotatedWithEncryptedInside.getField().getContent(object);
        if (fieldContent instanceof Iterable<?>) {
            ((Iterable<?>) fieldContent).forEach(item -> this.processFields(item, processor));
        } else if (fieldContent instanceof Map<?, ?>) {
            ((Map<?, ?>) fieldContent).forEach((k, v) -> this.processFields(v, processor));
        } else {
            this.processFields(fieldContent, processor);
        }
    }

    private void encryptField(Object object, FieldAnnotatedWithEncrypted fieldAnnotatedWithEncrypted) {
        try {
            var content = fieldAnnotatedWithEncrypted.getContentField().getContent(object);
            if (content == null) {
                return;
            }
            @Language("regexp")
            var pattern = fieldAnnotatedWithEncrypted.getEncryptedAnnotation().pattern();
            var encryptedContent = this.hissEncryptor.encrypt(content, pattern);
            fieldAnnotatedWithEncrypted.getContentField().setContent(object, encryptedContent);
            if (fieldAnnotatedWithEncrypted.getEncryptedAnnotation().hashingEnabled()) {
                var hashedContent = this.hissHasher.hash(content, pattern);
                fieldAnnotatedWithEncrypted.getHashField().setContent(object, hashedContent);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void decryptField(Object object, FieldAnnotatedWithEncrypted fieldAnnotatedWithEncrypted) {
        try {
            var content = fieldAnnotatedWithEncrypted.getContentField().getContent(object);
            var decryptedContent = this.hissEncryptor.decrypt(content);
            fieldAnnotatedWithEncrypted.getContentField().setContent(object, decryptedContent);
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
            getFieldAnnotatedWithEncrypted(clazz, field).ifPresent(fieldsAnnotatedWithEncrypted::add);
            getFieldAnnotatedWithEncryptedInside(clazz, field).ifPresent(fieldsAnnotatedWithEncryptedInside::add);
        }

        var classDescription = new ClassDescription(fieldsAnnotatedWithEncrypted, fieldsAnnotatedWithEncryptedInside);
        CLASSES_DESCRIPTION_CACHE.put(clazz, classDescription);
        if (CLASSES_DESCRIPTION_CACHE.size() > 10000) {
            logger.log(Level.WARNING, "{0} classes are cached", CLASSES_DESCRIPTION_CACHE.size());
        }
        return classDescription;
    }

    private static Optional<FieldAnnotatedWithEncrypted>
    getFieldAnnotatedWithEncrypted(Class<?> clazz, Field field) {
        var encryptedAnnotation = field.getDeclaredAnnotation(Encrypted.class);
        if (encryptedAnnotation != null) {
            var contentField = new StringField(clazz, field.getName());
            var hashField = getHashField(clazz, field, encryptedAnnotation);
            return Optional.of(new FieldAnnotatedWithEncrypted(encryptedAnnotation, contentField, hashField));
        } else {
            return Optional.empty();
        }
    }

    private static Optional<FieldAnnotatedWithEncryptedInside>
    getFieldAnnotatedWithEncryptedInside(Class<?> clazz, Field field) {
        var encryptedInsideAnnotation = field.getDeclaredAnnotation(EncryptedInside.class);
        if (encryptedInsideAnnotation != null) {
            return Optional.of(new FieldAnnotatedWithEncryptedInside(new ReadOnlyObjectField(clazz, field.getName())));
        } else {
            return Optional.empty();
        }
    }

    private static StringField getHashField(Class<?> clazz, Field field, Encrypted encryptedAnnotation) {
        if (encryptedAnnotation.hashingEnabled()) {
            if (StringUtils.hasText(encryptedAnnotation.hashFieldName())) {
                return new StringField(clazz, encryptedAnnotation.hashFieldName());
            } else {
                return new StringField(clazz, "hashed" + StringUtils.capitalizeFirstLetter(field.getName()));
            }
        }
        return null;
    }

    @Value
    private static class ClassDescription {
        List<FieldAnnotatedWithEncrypted> fieldsAnnotatedWithEncrypted;
        List<FieldAnnotatedWithEncryptedInside> fieldsAnnotatedWithEncryptedInside;
    }

    @Value
    private static class FieldAnnotatedWithEncryptedInside {
        ReadOnlyObjectField field;
    }

    @Value
    private static class FieldAnnotatedWithEncrypted {
        Encrypted encryptedAnnotation;
        StringField contentField;
        StringField hashField;
    }

    private static class StringField {
        private final Method getter;
        private final Method setter;

        public StringField(Class<?> clazz, String fieldName) {
            this.getter = ReflectionUtils.getMethod(clazz,
                    "get" + StringUtils.capitalizeFirstLetter(fieldName));
            this.setter = ReflectionUtils.getMethod(clazz,
                    "set" + StringUtils.capitalizeFirstLetter(fieldName), String.class);
        }

        public String getContent(Object object) {
            return ReflectionUtils.invokeSupplier(object, getter, String.class);
        }

        public void setContent(Object object, String content) {
            try {
                setter.invoke(object, content);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class ReadOnlyObjectField {
        private final Method getter;

        public ReadOnlyObjectField(Class<?> clazz, String fieldName) {
            this.getter = ReflectionUtils.getMethod(clazz,
                    "get" + StringUtils.capitalizeFirstLetter(fieldName));
        }

        public Object getContent(Object object) {
            return ReflectionUtils.invokeSupplier(object, getter, Object.class);
        }

    }

}
