package io.github.tap30.hiss;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class EncryptedInsideAnnotationOnMapTest extends BaseHissTest {

    @Test
    void encryptAndDecryptDomainObject() {
        // Given

        var user = User.builder()
                .id("5")
                .phoneNumber("123456789")
                .addressMap(Map.of(
                        "address1",
                        Address.builder()
                                .city("list city 1")
                                .postalCode("LC1")
                                .build(),
                        "address2",
                        Address.builder()
                                .city("list city 2")
                                .postalCode("LC2")
                                .build()
                ))
                .build();

        // When
        this.hiss.encryptObject(user);

        // Then
        {
            assertEquals("5", user.getId());
            assertNotEquals("123456789", user.getPhoneNumber());

            assertEquals(2, user.getAddressMap().size());
            var listAddress1 = user.getAddressMap().get("address1");
            assertEquals("list city 1", listAddress1.getCity());
            assertNotEquals("LC1", listAddress1.getPostalCode());
            var listAddress2 = user.getAddressMap().get("address2");
            assertEquals("list city 2", listAddress2.getCity());
            assertNotEquals("LC2", listAddress2.getPostalCode());
        }


        // When
        this.hiss.decryptObject(user);

        // Then
        {
            assertEquals("5", user.getId());
            assertEquals("123456789", user.getPhoneNumber());

            assertEquals(2, user.getAddressMap().size());
            var listAddress1 = user.getAddressMap().get("address1");
            assertEquals("list city 1", listAddress1.getCity());
            assertEquals("LC1", listAddress1.getPostalCode());
            var listAddress2 = user.getAddressMap().get("address2");
            assertEquals("list city 2", listAddress2.getCity());
            assertEquals("LC2", listAddress2.getPostalCode());
        }
    }

    @Getter
    @Setter
    @Builder
    public static class User {
        private String id;
        @Encrypted(hashingEnabled = false)
        private String phoneNumber;
        @EncryptedInside
        private Map<String, Address> addressMap;
    }

    @Getter
    @Setter
    @Builder
    public static class Address {
        private String city;
        @Encrypted(hashingEnabled = false)
        private String postalCode;
    }

}
