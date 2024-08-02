package io.github.tap30.hiss;

import io.github.tap30.hiss.key.Key;
import io.github.tap30.hiss.properties.HissProperties;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HissFactoryTest {

    @Test
    void testCreateHiss() {
        // Given
        var properties = new HissProperties() {

            @Override
            protected Set<Key> loadKeys() {
                return Set.of(Key.builder()
                                .id("default_key")
                                .key(Base64.getDecoder().decode("AAAAAAAAAAAAAAAAAAAAAA=="))
                                .keyHash("$2a$12$3T0VMnGMgvesehYomommnO02dbFOJuM/3elsmgmsB2/qlGSF3BIbe")
                        .build());
            }

            @Override
            protected String loadDefaultEncryptionKeyId() {
                return "default_key";
            }

            @Override
            protected String loadDefaultEncryptionAlgorithm() {
                return "aes-128-gcm";
            }

            @Override
            protected String loadDefaultHashingKeyId() {
                return "default_key";
            }

            @Override
            protected String loadDefaultHashingAlgorithm() {
                return "hmac-sha256";
            }

            @Override
            protected boolean loadKeyHashGenerationEnabled() {
                return false;
            }
        };

        // When
        var hiss = HissFactory.createHiss(properties);

        // Then
        assertNotNull(hiss);
        assertEquals("#$$#{hmac-sha256:default_key}{izfsg2N2nlwGtgNPzfwTWFUFIb5xJTvV5qEsRRUODmk=}#$$#",
                hiss.hash("some encrypted text"));
        assertEquals("some encrypted text",
                hiss.decrypt("#$$#{aes-128-gcm:default_key}{nYf5c6FQYJCQdc6JcfqTkvaSwqTGg2Oh0fapibp94G4anlMeXZrCAOZLOhMD3QIJymv/}#$$#"));
    }

    @Test
    void testCreateHiss_shouldPropertiesBeingValidated() {
        assertThrows(IllegalArgumentException.class, () ->
                HissFactory.createHiss(new HissProperties() {
                    @Override
                    protected Set<Key> loadKeys() {
                        return Set.of();
                    }

                    @Override
                    protected String loadDefaultEncryptionKeyId() {
                        return "";
                    }

                    @Override
                    protected String loadDefaultEncryptionAlgorithm() {
                        return "";
                    }

                    @Override
                    protected String loadDefaultHashingKeyId() {
                        return "";
                    }

                    @Override
                    protected String loadDefaultHashingAlgorithm() {
                        return "";
                    }

                    @Override
                    protected boolean loadKeyHashGenerationEnabled() {
                        return false;
                    }
                }));
    }

}