package io.github.tap30.hiss;

import io.github.tap30.hiss.properties.HissPropertiesFromEnv;
import org.junit.jupiter.api.BeforeEach;

import java.util.Map;

public class BaseHissTest {
    protected Hiss hiss;

    @BeforeEach
    void setUpHiss() {
        var hissPropertiesFromEnv = new HissPropertiesFromEnv();
        hissPropertiesFromEnv.setEnvProvider(() -> Map.of(
                "HISS_DEFAULT_ENCRYPTION_KEY_ID", "default_key",
                "HISS_DEFAULT_ENCRYPTION_ALGORITHM", "aes-128-gcm",
                "HISS_DEFAULT_HASHING_KEY_ID", "default_key",
                "HISS_DEFAULT_HASHING_ALGORITHM", "hmac-sha256",
                "HISS_KEYS_DEFAULT_KEY", "AAAAAAAAAAAAAAAAAAAAAA==",
                "HISS_KEYS_DEFAULT_KEY___HASH", "$2a$12$3T0VMnGMgvesehYomommnO02dbFOJuM/3elsmgmsB2/qlGSF3BIbe", // todo: remove from here and add it to separate test
                "HISS_KEY_HASH_GENERATION_ENABLED", "true"
        ));
        hiss = HissFactory.createHiss(hissPropertiesFromEnv);
    }

}
