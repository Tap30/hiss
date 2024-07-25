package io.github.tap30.hiss;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// todo: improve doc
/**
 * Fields annotated with this will be scanned
 */
// todo: add example
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EncryptedInside {
}
