package io.github.tap30.hiss;

import org.junit.jupiter.api.BeforeEach;

import java.util.Map;

public class BaseHissTest {
    protected Hiss hiss;

    @BeforeEach
    void setUpHiss() {
        hiss = HissFactory.createHiss(() -> HissProperties.fromBase64EncodedKeys(
                Map.of("default_key", "AAAAAAAAAAAAAAAAAAAAAA=="),
                "default_key",
                "aes-128-gcm",
                "default_key",
                "hmac-sha256"
        ));
    }

}
