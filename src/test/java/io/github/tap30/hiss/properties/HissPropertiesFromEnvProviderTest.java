package io.github.tap30.hiss.properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SystemStubsExtension.class)
class HissPropertiesFromEnvProviderTest {

    @SystemStub
    EnvironmentVariables environment = new EnvironmentVariables(
            "HISS_DEFAULT_ENCRYPTION_KEY_ID", "default_enc_key",
            "HISS_DEFAULT_ENCRYPTION_ALGORITHM", "aes-128-gcm",
            "HISS_DEFAULT_HASHING_KEY_ID", "default_hash_key",
            "HISS_DEFAULT_HASHING_ALGORITHM", "hmac-sha256",
            "HISS_KEYS_DEFAULT_KEY", "dGhlIGFjdHVhbCBrZXkK",
            "HISS_KEYS_DEFAULT_KEY___HASH", "some hash",
            "HISS_KEYS_OTHER_KEY", "dGhlIGFjdHVhbCBvdGhlciBrZXkK",
            "HISS_KEYS_OTHER_KEY___HASH", "other key hash",
            "HISS_KEY_HASH_GENERATION_ENABLED", "true"
    );

    @Test
    void test() {
        // Given & When
        var hissProperties = HissProperties.fromEnv();

        // Then
        assertEquals(2, hissProperties.getKeys().size());
        assertEquals("default_key", hissProperties.getKeys().get("default_key").getId());
        assertArrayEquals("the actual key\n".getBytes(StandardCharsets.US_ASCII), hissProperties.getKeys().get("default_key").getKey());
        assertEquals("some hash", hissProperties.getKeys().get("default_key").getKeyHash());
        assertEquals("other_key", hissProperties.getKeys().get("other_key").getId());
        assertArrayEquals("the actual other key\n".getBytes(StandardCharsets.US_ASCII), hissProperties.getKeys().get("other_key").getKey());
        assertEquals("other key hash", hissProperties.getKeys().get("other_key").getKeyHash());
        assertEquals("default_enc_key", hissProperties.getDefaultEncryptionKeyId());
        assertEquals("aes-128-gcm", hissProperties.getDefaultEncryptionAlgorithm());
        assertEquals("default_hash_key", hissProperties.getDefaultHashingKeyId());
        assertEquals("hmac-sha256", hissProperties.getDefaultHashingAlgorithm());
    }

}