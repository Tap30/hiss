package io.github.tap30.hiss.hasher.impl;

public class TapsiHmacSha256Hasher extends HmacSha256Hasher {
    @Override
    public String getName() {
        return "hmac-sha256";
    }
}
