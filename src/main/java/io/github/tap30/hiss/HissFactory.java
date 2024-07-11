package io.github.tap30.hiss;

import io.github.tap30.hiss.properties.HissProperties;

public class HissFactory {

    public static Hiss createHiss(HissProperties hissProperties) {
        return new Hiss(hissProperties);
    }

}
