package io.github.tap30.hiss;

public class HissFactory {

    public static Hiss createHiss(HissPropertiesProvider hissPropertiesProvider) {
        return new Hiss(hissPropertiesProvider.getProperties());
    }

}
