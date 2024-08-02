package io.github.tap30.hiss.properties;

import io.github.tap30.hiss.key.Key;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class HissProperties {

    private final Map<String, Object> properties = new HashMap<>();

    public Map<String, Key> getKeys() {
        return this.getProperty("Keys", () -> loadKeys()
                .stream()
                .collect(Collectors.toMap(Key::getId, k -> k)));
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

    public boolean isKeyHashGenerationEnabled() {
        return this.getProperty("KeyHashGenerationEnabled", this::loadKeyHashGenerationEnabled);
    }

    protected abstract Set<Key> loadKeys();

    protected abstract String loadDefaultEncryptionKeyId();

    protected abstract String loadDefaultEncryptionAlgorithm();

    protected abstract String loadDefaultHashingKeyId();

    protected abstract String loadDefaultHashingAlgorithm();

    protected abstract boolean loadKeyHashGenerationEnabled();

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
