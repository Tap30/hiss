package io.github.tap30.hiss;

import io.github.tap30.hiss.properties.HissPropertiesProvider;

public class HissFactory {

    public static Hiss createHiss(HissPropertiesProvider hissPropertiesProvider) {
        return new Hiss(hissPropertiesProvider.getProperties());
    }

}
