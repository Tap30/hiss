package io.github.tap30.hiss;

import org.intellij.lang.annotations.Identifier;
import org.intellij.lang.annotations.Language;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fields annotated using this will be encrypted.
 */
// Todo: add example
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Encrypted {
    /**
     * @return the pattern which only matched content will be encrypted;
     * empty or null values mean all content should be encrypted.
     */
    @Language("regexp")
    String pattern() default "";

    /**
     * @return that should we calculate and store hash of content.
     * @see #hashFieldName()
     */
    boolean hashingEnabled() default true;

    /**
     * @return name of the field in which hashed content will be put;
     * empty or null values mean the name will be guessed.
     * <br>
     * The guessing algorithm is by concatenating "hashed" and first-letter-capitalized field name;
     * e.g. if the name of the encrypted field is <code>phoneNumber</code>,
     * guessed hashed field name is <code>hashedPhoneNumber</code>.
     */
    @Identifier
    String hashFieldName() default "";
}
