package io.github.tap30.hissapp.model;

import io.github.tap30.hiss.Encrypted;
import io.github.tap30.hiss.EncryptedInside;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String id;

    private String name;

    @Encrypted(pattern = "\\d{4}$")
    private String phoneNumber;
    private String hashedPhoneNumber;

    @EncryptedInside
    private List<Address> addresses;

}
