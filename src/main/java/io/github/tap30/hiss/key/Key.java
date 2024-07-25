package io.github.tap30.hiss.key;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Key {
    String id;
    byte[] key;
    String keyHash;
}
