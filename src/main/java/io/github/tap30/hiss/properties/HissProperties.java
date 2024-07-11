package io.github.tap30.hiss.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class HissProperties {

    private final Map<String, Object> properties = new HashMap<>();

    public Map<String, byte[]> getKeys() {
        return this.getProperty("Keys", this::loadKeys);
    }

    public String getDefaultEncryptionKeyId() {
        return this.getProperty("DefaultEncryptionKeyId", this::loadDefaultEncryptionKeyId);
    }

    public String getDefaultEncryptionAlgorithm() {
        return this.getProperty("DefaultEncryptionAlgorithm", this::loadDefaultEncryptionAlgorithm);
    }

    public String getDefaultHashingKeyId() {
        return this.getProperty("DefaultHashingKeyId", this::loadDefaultHashingKeyId);
    }

    public String getDefaultHashingAlgorithm() {
        return this.getProperty("DefaultHashingAlgorithm", this::loadDefaultHashingAlgorithm);
    }

    protected abstract Map<String, byte[]> loadKeys();
    protected abstract String loadDefaultEncryptionKeyId();
    protected abstract String loadDefaultEncryptionAlgorithm();
    protected abstract String loadDefaultHashingKeyId();
    protected abstract String loadDefaultHashingAlgorithm();

    @SuppressWarnings("unchecked")
    private <T> T getProperty(String key, Supplier<T> valueSupplier) {
        if (this.properties.containsKey(key)) {
            return (T) this.properties.get(key);
        }
        var value = valueSupplier.get();
        this.properties.put(key, value);
        return value;
    }

}
