package io.github.tap30.hissapp.model;

import io.github.tap30.hiss.Encrypted;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Secret {

    @Encrypted(hashingEnabled = false)
    private String secret;

    private Set<String> applications;

}
