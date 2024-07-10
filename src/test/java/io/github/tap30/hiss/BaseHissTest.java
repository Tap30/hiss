package io.github.tap30.hiss;

import io.github.tap30.hiss.properties.HissPropertiesFromEnvProvider;
import org.junit.jupiter.api.BeforeEach;

import java.util.Map;

public class BaseHissTest {
    protected Hiss hiss;

    @BeforeEach
    void setUpHiss() {
        var hissPropertiesFromEnvProvider = new HissPropertiesFromEnvProvider();
        hissPropertiesFromEnvProvider.setEnvProvider(() -> Map.of(
                "HISS_DEFAULT_ENCRYPTION_KEY_ID", "default_key",
                "HISS_DEFAULT_ENCRYPTION_ALGORITHM", "aes-128-gcm",
                "HISS_DEFAULT_HASHING_KEY_ID", "default_key",
                "HISS_DEFAULT_HASHING_ALGORITHM", "hmac-sha256",
                "HISS_KEYS_DEFAULT_KEY", "AAAAAAAAAAAAAAAAAAAAAA=="
        ));
        hiss = HissFactory.createHiss(hissPropertiesFromEnvProvider);
    }

}
