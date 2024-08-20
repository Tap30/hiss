package io.github.tap30.hiss.properties;

import io.github.tap30.hiss.key.Key;
import io.github.tap30.hiss.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class HissProperties {

    private final Map<String, Object> properties = new HashMap<>();

    public Map<String, Key> getKeys() {
        return this.getProperty("Keys", this::loadKeys, HissProperties::keysAsMap);
    }

    public String getDefaultEncryptionKeyId() {
        return this.getProperty("DefaultEncryptionKeyId",
                this::loadDefaultEncryptionKeyId, StringUtils::toLowerCase);
    }

    public String getDefaultEncryptionAlgorithm() {
        return this.getProperty("DefaultEncryptionAlgorithm",
                this::loadDefaultEncryptionAlgorithm, StringUtils::toLowerCase);
    }

    public String getDefaultHashingKeyId() {
        return this.getProperty("DefaultHashingKeyId",
                this::loadDefaultHashingKeyId, StringUtils::toLowerCase);
    }

    public String getDefaultHashingAlgorithm() {
        return this.getProperty("DefaultHashingAlgorithm",
                this::loadDefaultHashingAlgorithm, StringUtils::toLowerCase);
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

    private <O> O getProperty(String key, Supplier<O> valueSupplier) {
        return getProperty(key, valueSupplier, v -> v);
    }

    @SuppressWarnings("unchecked")
    private <I, O> O getProperty(String key, Supplier<I> valueSupplier, Function<I, O> mapper) {
        if (this.properties.containsKey(key)) {
            return (O) this.properties.get(key);
        }
        var value = mapper.apply(valueSupplier.get());
        this.properties.put(key, value);
        return value;
    }

    private static Map<String, Key> keysAsMap(Set<Key> keys) {
        return keys.stream().collect(Collectors.toMap(k -> StringUtils.toLowerCase(k.getId()), k -> k));
    }

}
