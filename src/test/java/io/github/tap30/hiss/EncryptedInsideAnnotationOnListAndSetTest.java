package io.github.tap30.hiss;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class EncryptedInsideAnnotationOnListAndSetTest extends BaseHissTest {

    @Test
    void encryptAndDecryptDomainObject() {
        // Given

        var user = User.builder()
                .id("5")
                .phoneNumber("123456789")
                .addressList(List.of(
                        Address.builder()
                                .city("list city 1")
                                .postalCode("LC1")
                                .build(),
                        Address.builder()
                                .city("list city 2")
                                .postalCode("LC2")
                                .build()
                ))
                .addressSet(Set.of(
                        Address.builder()
                                .city("set city 1")
                                .postalCode("SC1")
                                .build(),
                        Address.builder()
                                .city("set city 2")
                                .postalCode("SC2")
                                .build()
                ))
                .build();

        // When
        this.hiss.encryptObject(user);

        // Then
        {
            assertEquals("5", user.getId());
            assertNotEquals("123456789", user.getPhoneNumber());

            assertEquals(2, user.getAddressList().size());
            var listAddress1 = user.getAddressList().get(0);
            assertEquals("list city 1", listAddress1.getCity());
            assertNotEquals("LC1", listAddress1.getPostalCode());
            var listAddress2 = user.getAddressList().get(1);
            assertEquals("list city 2", listAddress2.getCity());
            assertNotEquals("LC2", listAddress2.getPostalCode());

            assertEquals(2, user.getAddressSet().size());
            var setAddress1 = user.getAddressSet().stream().filter(a -> a.getCity().equals("set city 1")).findFirst().get();
            assertEquals("set city 1", setAddress1.getCity());
            assertNotEquals("SC1", setAddress1.getPostalCode());
            var setAddress2 = user.getAddressSet().stream().filter(a -> a.getCity().equals("set city 2")).findFirst().get();
            assertEquals("set city 2", setAddress2.getCity());
            assertNotEquals("SC2", setAddress2.getPostalCode());
        }


        // When
        this.hiss.decryptObject(user);

        // Then
        {
            assertEquals("5", user.getId());
            assertEquals("123456789", user.getPhoneNumber());

            assertEquals(2, user.getAddressList().size());
            var listAddress1 = user.getAddressList().get(0);
            assertEquals("list city 1", listAddress1.getCity());
            assertEquals("LC1", listAddress1.getPostalCode());
            var listAddress2 = user.getAddressList().get(1);
            assertEquals("list city 2", listAddress2.getCity());
            assertEquals("LC2", listAddress2.getPostalCode());

            assertEquals(2, user.getAddressSet().size());
            var setAddress1 = user.getAddressSet().stream().filter(a -> a.getCity().equals("set city 1")).findFirst().get();
            assertEquals("set city 1", setAddress1.getCity());
            assertEquals("SC1", setAddress1.getPostalCode());
            var setAddress2 = user.getAddressSet().stream().filter(a -> a.getCity().equals("set city 2")).findFirst().get();
            assertEquals("set city 2", setAddress2.getCity());
            assertEquals("SC2", setAddress2.getPostalCode());
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
        private List<Address> addressList;
        @EncryptedInside
        private Set<Address> addressSet;
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
