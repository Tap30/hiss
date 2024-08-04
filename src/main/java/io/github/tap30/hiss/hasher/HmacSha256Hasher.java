package io.github.tap30.hiss.hasher;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacSha256Hasher implements Hasher {

    private static final String HMAC_SHA256 = "HmacSHA256";

    @Override
    public byte[] hash(byte[] key, byte[] content) throws Exception {
        var secretKeySpec = new SecretKeySpec(key, HMAC_SHA256);
        var mac = Mac.getInstance(HMAC_SHA256);
        mac.init(secretKeySpec);
        return mac.doFinal(content);
    }

    @Override
    public String getName() {
        return HMAC_SHA256;
    }
}
