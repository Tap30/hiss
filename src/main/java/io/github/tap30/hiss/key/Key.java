package io.github.tap30.hiss.key;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Key {
    /**
     * The ID (name) of the key.
     */
    String id;
    /**
     * The key itself.
     */
    byte[] key;
    /**
     * The hash of the key generated by Hiss itself.
     * Used to validate the key to prevent accidental key modification.
     */
    String keyHash;
}
