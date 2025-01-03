package io.github.tap30.hiss;

import io.github.tap30.hiss.key.Key;
import io.github.tap30.hiss.key.KeyHashGenerator;
import io.github.tap30.hiss.properties.HissProperties;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class HissFactoryTest {

    @Test
    void createHiss() {
        // Given
        var properties = HissProperties.builder()
                .keys(Set.of(Key.builder()
                        .id("default_key")
                        .key(Base64.getDecoder().decode("AAAAAAAAAAAAAAAAAAAAAA=="))
                        .keyHash("$2a$12$3T0VMnGMgvesehYomommnO02dbFOJuM/3elsmgmsB2/qlGSF3BIbe")
                        .build()))
                .defaultEncryptionKeyId("default_key")
                .defaultEncryptionAlgorithm("aes-128-gcm")
                .defaultHashingKeyId("default_key")
                .defaultHashingAlgorithm("hmac-sha256")
                .keyHashGenerationEnabled(true)
                .build();
        var keyHashGenerator = mock(KeyHashGenerator.class);
        HissFactory.keyHashGeneratorProvider = () -> keyHashGenerator;

        // When
        var hiss = HissFactory.createHiss(properties);

        // Then
        assertNotNull(hiss);
        assertEquals("#$$#{hmac-sha256:default_key}{izfsg2N2nlwGtgNPzfwTWFUFIb5xJTvV5qEsRRUODmk=}#$$#",
                hiss.hash("some encrypted text"));
        assertEquals("some encrypted text",
                hiss.decrypt("#$$#{aes-128-gcm:default_key}{nYf5c6FQYJCQdc6JcfqTkvaSwqTGg2Oh0fapibp94G4anlMeXZrCAOZLOhMD3QIJymv/}#$$#"));
        verify(keyHashGenerator).validateKeyHashes(any());
        verify(keyHashGenerator).generateAndLogHashes(any());
    }

    @Test
    void createHiss_shouldPropertiesBeingValidated() {
        assertThrows(IllegalArgumentException.class, () ->
                HissFactory.createHiss(HissProperties.builder().build()));
    }

}