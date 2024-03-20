package tech.tapsi.libs.hiss;

public class HissFactory {

    public static Hiss createHiss(HissPropertiesProvider hissPropertiesProvider) {
        return new Hiss(hissPropertiesProvider.getProperties());
    }

}
