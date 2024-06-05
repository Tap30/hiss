package io.github.tap30.hiss;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HissTest_EncryptedInsideOnSimpleObject extends BaseHissTest {

    @Test
    void testEncryptAndDecryptDomainObject() {
        // Given
        var latlng = new LatLng();
        latlng.setLat("31.3");
        latlng.setLng("82.5");
        latlng.setUpdatedAt("Yesterday");

        var address = new Address();
        address.setCity("tehran");
        address.setStreet("enghelab");
        address.setPlate("70");
        address.setUnit("3");
        address.setLatLng(latlng);

        var user = new User();
        user.setId("5");
        user.setPhoneNumber("123456789");
        user.setAddress(address);


        // When
        this.hiss.encryptObject(user);

        // Then
        assertEquals("5", user.getId());
        assertNotEquals("123456789", user.getPhoneNumber());
        assertNotNull(user.getHashedPhoneNumber());
        assertEquals("tehran", user.getAddress().getCity());
        assertEquals("enghelab", user.getAddress().getStreet());
        assertNotEquals("70", user.getAddress().getPlate());
        assertNotNull(user.getAddress().getHashedPlate());
        assertNotEquals("3", user.getAddress().getUnit());
        assertNotNull(user.getAddress().getHashedUnit());
        assertNotEquals("31.3", user.getAddress().getLatLng().getLat());
        assertNotEquals("82.5", user.getAddress().getLatLng().getLng());
        assertEquals("Yesterday", user.getAddress().getLatLng().getUpdatedAt());

        // When
        this.hiss.decryptObject(user);

        // Then
        assertEquals("5", user.getId());
        assertEquals("123456789", user.getPhoneNumber());
        assertNotNull(user.getHashedPhoneNumber());
        assertEquals("tehran", user.getAddress().getCity());
        assertEquals("enghelab", user.getAddress().getStreet());
        assertEquals("70", user.getAddress().getPlate());
        assertNotNull(user.getAddress().getHashedPlate());
        assertEquals("3", user.getAddress().getUnit());
        assertNotNull(user.getAddress().getHashedUnit());
        assertEquals("31.3", user.getAddress().getLatLng().getLat());
        assertEquals("82.5", user.getAddress().getLatLng().getLng());
        assertEquals("Yesterday", user.getAddress().getLatLng().getUpdatedAt());
    }

    @Getter
    @Setter
    public static class User {
        private String id;
        @Encrypted
        private String phoneNumber;
        private String hashedPhoneNumber;
        @EncryptedInside
        private Address address;
    }

    @Getter
    @Setter
    public static class Address {
        private String city;
        private String street;
        @Encrypted
        private String plate;
        private String hashedPlate;
        @Encrypted
        private String unit;
        private String hashedUnit;
        @EncryptedInside
        private LatLng latLng;
    }

    @Getter
    @Setter
    public static class LatLng {
        @Encrypted(hashingEnabled = false)
        private String lat;
        @Encrypted(hashingEnabled = false)
        private String lng;
        private String updatedAt;
    }


}
