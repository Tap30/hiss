package io.github.tap30.hissapp;

import io.github.tap30.hiss.HissFactory;
import io.github.tap30.hiss.properties.HissProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SystemStubsExtension.class)
public class ApplicationWithInvalidKeyHashes {

    @SystemStub
    EnvironmentVariables environment = new EnvironmentVariables(Application.environment.getVariables())
            .set("HISS_KEYS_DEFAULT_KEY___HASH", "bad hash")
            .set("HISS_KEYS_OLD_KEY___HASH", "bad hash");

    @Test
    void createHiss() {
        assertThrows(IllegalArgumentException.class, () -> HissFactory.createHiss(HissProperties.fromEnv()));
    }

}
