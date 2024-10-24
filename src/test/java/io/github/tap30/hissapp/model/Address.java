package io.github.tap30.hissapp.model;

import io.github.tap30.hiss.Encrypted;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    private String name;

    private String city;

    @Encrypted(hashingEnabled = false)
    private String street;

    @Encrypted
    private String postalCode;
    private String hashedPostalCode;

}
