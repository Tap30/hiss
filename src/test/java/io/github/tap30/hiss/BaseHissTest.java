package io.github.tap30.hiss;

import io.github.tap30.hiss.properties.HissPropertiesFromEnv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

@ExtendWith(SystemStubsExtension.class)
public class BaseHissTest {

    @SystemStub
    private EnvironmentVariables environment = new EnvironmentVariables(
            "HISS_DEFAULT_ENCRYPTION_KEY_ID", "default_key",
            "HISS_DEFAULT_ENCRYPTION_ALGORITHM", "aes-128-gcm",
            "HISS_DEFAULT_HASHING_KEY_ID", "default_key",
            "HISS_DEFAULT_HASHING_ALGORITHM", "hmac-sha256",
            "HISS_KEYS_DEFAULT_KEY", "AAAAAAAAAAAAAAAAAAAAAA==",
            "HISS_KEY_HASH_GENERATION_ENABLED", "false"
    );

    protected Hiss hiss;

    @BeforeEach
    void setUpHiss() {
        var hissPropertiesFromEnv = new HissPropertiesFromEnv();
        hiss = HissFactory.createHiss(hissPropertiesFromEnv);
    }

}
