package io.github.tap30.hiss.key;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.github.tap30.hiss.utils.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class KeyHashGenerator {

    private static final Logger logger = Logger.getLogger(KeyHashGenerator.class.getName());
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final BCrypt.Hasher hasher;
    private final BCrypt.Verifyer verifyer;

    public KeyHashGenerator(BCrypt.Hasher hasher, BCrypt.Verifyer verifyer) {
        this.hasher = hasher;
        this.verifyer = verifyer;
    }

    public void generateAndLogHashes(Collection<Key> keys) {
        var result = new StringBuilder();
        result.append("Keys' Hash:");
        generateHashes(keys).forEach((k, v) -> result.append("\n  ").append(k).append(": ").append(v));
        logger.log(Level.INFO, result.toString());
    }

    /**
     * @return map of key ID to key hash.
     */
    public Map<String, String> generateHashes(Collection<Key> keys) {
        var hashes = new HashMap<String, String>();
        keys.forEach(k -> hashes.put(k.getId(), new String(hasher.hash(12, k.getKey()), CHARSET)));
        return hashes;
    }

    /**
     * @return invalid key IDs.
     */
    public Set<String> validateKeyHashes(Collection<Key> keys) {
        return keys.stream()
                .filter(key -> {
                    if (StringUtils.hasText(key.getKeyHash())) {
                        return true;
                    } else {
                        logger.log(Level.WARNING,
                                "Key {0} does not have hash; supply it as soon as possible.",
                                key.getId());
                        return false;
                    }
                })
                .filter(key -> !verifyer.verify(key.getKey(), key.getKeyHash().getBytes(CHARSET)).verified)
                .map(Key::getId)
                .collect(Collectors.toSet());
    }

}
