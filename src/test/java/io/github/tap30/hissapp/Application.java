package io.github.tap30.hissapp;

import io.github.tap30.hiss.Encrypted;
import io.github.tap30.hiss.Hiss;
import io.github.tap30.hiss.HissFactory;
import io.github.tap30.hiss.properties.HissProperties;
import io.github.tap30.hissapp.model.Address;
import io.github.tap30.hissapp.model.Admin;
import io.github.tap30.hissapp.model.Secret;
import io.github.tap30.hissapp.model.User;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SystemStubsExtension.class)
public class Application {

    @SystemStub
    static EnvironmentVariables environment = new EnvironmentVariables(
            "HISS_DEFAULT_ENCRYPTION_KEY_ID", "default_key",
            "HISS_DEFAULT_ENCRYPTION_ALGORITHM", "aes-128-gcm",
            "HISS_DEFAULT_HASHING_KEY_ID", "default_key",
            "HISS_DEFAULT_HASHING_ALGORITHM", "hmac-sha256",
            "HISS_KEYS_DEFAULT_KEY", "AAAAAAAAAAAAAAAAAAAAAA==",
            "HISS_KEYS_DEFAULT_KEY___HASH", "$2a$12$3T0VMnGMgvesehYomommnO02dbFOJuM/3elsmgmsB2/qlGSF3BIbe",
            "HISS_KEYS_OLD_KEY", "AQIDBAUGBwgJCgsMDQ4PEA==",
            "HISS_KEYS_OLD_KEY___HASH", "$2a$12$THkoYZHlqD/HvrSkKUDs9eyHwY7W2FmyJm6SMp4xeGfP2g7F6Ro/i",
            "HISS_KEY_HASH_GENERATION_ENABLED", "true"
    );

    private static Hiss hiss;

    @BeforeAll
    static void setUpHiss() {
        hiss = HissFactory.createHiss(HissProperties.fromEnv());
    }

    @Test
    void encryptUser() {
        // Given
        var user = User.builder()
                .id("user-01")
                .name("Mostafa")
                .phoneNumber("+989123456789")
                .addresses(List.of(
                        Address.builder()
                                .name("home")
                                .city("Tehran")
                                .street("Enghelab")
                                .postalCode("1234567890")
                                .build(),
                        Address.builder()
                                .name("work")
                                .city("Mashhad")
                                .street("Azadi")
                                .postalCode("1234567891")
                                .build()
                ))
                .build();

        // When
        hiss.encryptObject(user);

        // Then
        assertEquals("user-01", user.getId());
        assertEquals("Mostafa", user.getName());
        assertTrue(user.getPhoneNumber().startsWith("+98912345"));
        assertFalse(user.getPhoneNumber().contains("6789"));
        assertEquals(2, user.getAddresses().size());
        {
            var address = user.getAddresses().get(0);
            assertEquals("home", address.getName());
            assertEquals("Tehran", address.getCity());
            assertFalse(address.getStreet().contains("Enghelab"));
            assertFalse(address.getPostalCode().contains("1234567890"));
        }
        {
            var address = user.getAddresses().get(1);
            assertEquals("work", address.getName());
            assertEquals("Mashhad", address.getCity());
            assertFalse(address.getStreet().contains("Azadi"));
            assertFalse(address.getPostalCode().contains("1234567891"));
        }

        // When
        hiss.decryptObject(user);


        // Then
        assertEquals("user-01", user.getId());
        assertEquals("Mostafa", user.getName());
        assertEquals("+989123456789", user.getPhoneNumber());
        assertEquals(2, user.getAddresses().size());
        {
            var address = user.getAddresses().get(0);
            assertEquals("home", address.getName());
            assertEquals("Tehran", address.getCity());
            assertEquals("Enghelab", address.getStreet());
            assertEquals("1234567890", address.getPostalCode());
        }
        {
            var address = user.getAddresses().get(1);
            assertEquals("work", address.getName());
            assertEquals("Mashhad", address.getCity());
            assertEquals("Azadi", address.getStreet());
            assertEquals("1234567891", address.getPostalCode());
        }
    }

    @Test
    void rotateUserAlgorithmAndKeyId() {
        // Given
        var user = User.builder()
                .id("user-01")
                .name("Mostafa")
                .phoneNumber("+98912345#$$#{aes-128-cbc:old_key}{/UjgtjsLutCrcYTJ/APxVTu0CLjWlElScSgBw7IGcwY=}#$$#")
                .addresses(List.of(
                        Address.builder()
                                .name("home")
                                .city("Tehran")
                                .street("#$$#{aes-128-cbc:old_key}{qhcgSbNLHfWzJS1vIUC23xdZWDdc6/L8RSf9nArTGN8=}#$$#")
                                .postalCode("#$$#{aes-128-cbc:old_key}{PjjcFEuslRRhP39s8oOYNGIxL8ta2748wpVu4SZkiBo=}#$$#")
                                .build(),
                        Address.builder()
                                .name("work")
                                .city("Mashhad")
                                .street("#$$#{aes-128-cbc:old_key}{anBz1DfneYckkZ2/Esh9MybXh0xWJIT0SCEBI9RtWoc=}#$$#")
                                .postalCode("#$$#{aes-128-cbc:old_key}{UtTWbxJTqRDcHRebijQpVROSN0NAgwR3qETjSSVKDyA=}#$$#")
                                .build()
                ))
                .build();

        // When
        hiss.decryptObject(user);


        // Then
        assertEquals("user-01", user.getId());
        assertEquals("Mostafa", user.getName());
        assertEquals("+989123456789", user.getPhoneNumber());
        assertEquals(2, user.getAddresses().size());
        {
            var address = user.getAddresses().get(0);
            assertEquals("home", address.getName());
            assertEquals("Tehran", address.getCity());
            assertEquals("Enghelab", address.getStreet());
            assertEquals("1234567890", address.getPostalCode());
        }
        {
            var address = user.getAddresses().get(1);
            assertEquals("work", address.getName());
            assertEquals("Mashhad", address.getCity());
            assertEquals("Azadi", address.getStreet());
            assertEquals("1234567891", address.getPostalCode());
        }
    }

    @Test
    void encryptAdmin() {
        // Given
        var admin = new Admin();
        admin.setId("admin-01");
        admin.setName("Mostafa");
        admin.setPhoneNumber("+989123456789");
        admin.setAddresses(List.of(
                Address.builder()
                        .name("home")
                        .city("Tehran")
                        .street("Enghelab")
                        .postalCode("1234567890")
                        .build(),
                Address.builder()
                        .name("work")
                        .city("Mashhad")
                        .street("Azadi")
                        .postalCode("1234567891")
                        .build()
        ));
        admin.setSecrets(Map.of(
                "secret-01", Secret.builder()
                        .secret("secret number one")
                        .applications(Set.of("app1", "app2"))
                        .build(),
                "secret-02", Secret.builder()
                        .secret("secret number two")
                        .applications(Set.of("app3", "app4"))
                        .build()
        ));

        // When
        hiss.encryptObject(admin);

        // Then
        assertEquals("admin-01", admin.getId());
        assertEquals("Mostafa", admin.getName());
        assertTrue(admin.getPhoneNumber().startsWith("+98912345"));
        assertFalse(admin.getPhoneNumber().contains("6789"));
        assertEquals(2, admin.getAddresses().size());
        {
            var address = admin.getAddresses().get(0);
            assertEquals("home", address.getName());
            assertEquals("Tehran", address.getCity());
            assertFalse(address.getStreet().contains("Enghelab"));
            assertFalse(address.getPostalCode().contains("1234567890"));
        }
        {
            var address = admin.getAddresses().get(1);
            assertEquals("work", address.getName());
            assertEquals("Mashhad", address.getCity());
            assertFalse(address.getStreet().contains("Azadi"));
            assertFalse(address.getPostalCode().contains("1234567891"));
        }
        assertEquals(2, admin.getSecrets().size());
        {
            var secret = admin.getSecrets().get("secret-01");
            assertFalse(secret.getSecret().contains("secret number one"));
            assertEquals(Set.of("app1", "app2"), secret.getApplications());
        }
        {
            var secret = admin.getSecrets().get("secret-02");
            assertFalse(secret.getSecret().contains("secret number two"));
            assertEquals(Set.of("app3", "app4"), secret.getApplications());
        }

        // When
        hiss.decryptObject(admin);


        // Then
        assertEquals("admin-01", admin.getId());
        assertEquals("Mostafa", admin.getName());
        assertEquals("+989123456789", admin.getPhoneNumber());

        assertEquals(2, admin.getAddresses().size());
        {
            var address = admin.getAddresses().get(0);
            assertEquals("home", address.getName());
            assertEquals("Tehran", address.getCity());
            assertEquals("Enghelab", address.getStreet());
            assertEquals("1234567890", address.getPostalCode());
        }
        {
            var address = admin.getAddresses().get(1);
            assertEquals("work", address.getName());
            assertEquals("Mashhad", address.getCity());
            assertEquals("Azadi", address.getStreet());
            assertEquals("1234567891", address.getPostalCode());
        }
        assertEquals(2, admin.getSecrets().size());
        {
            var secret = admin.getSecrets().get("secret-01");
            assertEquals("secret number one", secret.getSecret());
            assertEquals(Set.of("app1", "app2"), secret.getApplications());
        }
        {
            var secret = admin.getSecrets().get("secret-02");
            assertEquals("secret number two", secret.getSecret());
            assertEquals(Set.of("app3", "app4"), secret.getApplications());
        }
    }

    @Test
    void encryptString() {
        // Given
        final var text = "plain text";

        // When
        var encryptedText = hiss.encrypt(text);
        encryptedText = hiss.encrypt(encryptedText);
        encryptedText = hiss.encrypt(encryptedText);

        // Then
        assertFalse(encryptedText.contains(text));

        // When
        var decryptedText = hiss.decrypt(encryptedText);
        decryptedText = hiss.decrypt(decryptedText);
        decryptedText = hiss.decrypt(decryptedText);

        // Then
        assertEquals(text, decryptedText);
    }

    @Test
    void encryptString_withPattern() {
        // Given
        final var text = "Your code is 12345. Keep it safe.";

        // When
        var encryptedText = hiss.encrypt(text, "\\d+");
        encryptedText = hiss.encrypt(encryptedText, "\\d+");
        encryptedText = hiss.encrypt(encryptedText, "\\d+");

        // Then
        assertFalse(encryptedText.contains("12345"));
        assertTrue(encryptedText.startsWith("Your code is "));
        assertTrue(encryptedText.endsWith(". Keep it safe."));

        // When
        var decryptedText = hiss.decrypt(encryptedText);
        decryptedText = hiss.decrypt(decryptedText);
        decryptedText = hiss.decrypt(decryptedText);

        // Then
        assertEquals(text, decryptedText);
    }

    @Test
    void decryptString() {
        // Given
        var encryptedText = "#$$#{aes-128-gcm:default_key}{dZdE50gZRAtgzQ9ar2hemaWg0flEL9/SO8CoaZ+K12u6mDirOSaIeA==}#$$#";

        // When
        var decryptedText = hiss.decrypt(encryptedText);

        // Then
        assertEquals("Enghelab", decryptedText);
    }

    @Test
    void decryptString_withPattern() {
        // Given
        var encryptedText = "+98912345#$$#{aes-128-gcm:default_key}{ha8e/UDZmsLuAqGW9zGo7eaq5e8cM79OO7Mp2ZUThcup2+8O}#$$#";

        // When
        var decryptedText = hiss.decrypt(encryptedText);

        // Then
        assertEquals("+989123456789", decryptedText);
    }

    @Test
    void decryptString_withKeyIdAndAlgorithmDifferentFromDefaults() {
        // Given
        var encryptedText = "#$$#{aes-128-cbc:old_key}{UtTWbxJTqRDcHRebijQpVROSN0NAgwR3qETjSSVKDyA=}#$$#";

        // When
        var decryptedText = hiss.decrypt(encryptedText);

        // Then
        assertEquals("1234567891", decryptedText);
    }

    @Test
    void hashString() {
        // Given
        final var text = "plain text";

        // When
        var hashedText1 = hiss.hash(text);
        var hashedText2 = hiss.hash(hashedText1);
        var hashedText3 = hiss.hash(hashedText1);

        // Then
        assertEquals(hashedText1, hashedText2);
        assertEquals(hashedText2, hashedText3);
        assertFalse(hashedText1.contains(text));
    }

    @Test
    void hashString_withPattern() {
        // Given
        final var text = "Your code is 12345. Keep it safe.";

        // When
        var hashedText1 = hiss.hash(text, "\\d+");
        var hashedText2 = hiss.hash(hashedText1, "\\d+");
        var hashedText3 = hiss.hash(hashedText2, "\\d+");

        // Then
        assertEquals(hashedText1, hashedText2);
        assertEquals(hashedText2, hashedText3);
        assertFalse(hashedText1.contains("12345"));
        assertTrue(hashedText1.startsWith("Your code is "));
        assertTrue(hashedText1.endsWith(". Keep it safe."));
    }

    @Test
    void test() {
        var message = new Message();
        message.setContent("User 123 called you.");

        hiss.encryptObject(message);

        System.out.println(message.getContent());
        System.out.println(message.getHashedContent());
    }

    @Getter
    @Setter
    public static class Message {

        @Encrypted(pattern = "\\d+")
        private String content;
        private String hashedContent;

        // getters and setters ...
    }

}
