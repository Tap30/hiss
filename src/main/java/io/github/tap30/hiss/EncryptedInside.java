package io.github.tap30.hiss;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Todo: improve doc
/**
 * Fields annotated with this will be scanned
 */
// Todo: add example
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EncryptedInside {
}
