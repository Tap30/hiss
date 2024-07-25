package io.github.tap30.hiss;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.github.tap30.hiss.key.KeyHashGenerator;
import io.github.tap30.hiss.properties.HissProperties;
import io.github.tap30.hiss.properties.HissPropertiesValidator;

import java.util.logging.Logger;

public class HissFactory {

    private static final Logger logger = Logger.getLogger(HissFactory.class.getName());

    /**
     * Creates a Hiss instance with provided <code>HissProperties</code>.
     *
     * @param hissProperties the properties by which hiss will be instantiated;
     *                       {@link io.github.tap30.hiss.properties.HissPropertiesFromEnv}
     *                       or any custom implementation of
     *                       {@link io.github.tap30.hiss.properties.HissProperties}
     *                       can be used.
     * @return {@link Hiss} instance.
     * @throws IllegalArgumentException if the properties are not valid.
     */
    public static Hiss createHiss(HissProperties hissProperties) {
        var keyHashGenerator = new KeyHashGenerator(BCrypt.withDefaults(), BCrypt.verifyer());
        new HissPropertiesValidator(keyHashGenerator).validate(hissProperties);
        return new Hiss(hissProperties, keyHashGenerator);
    }

}
