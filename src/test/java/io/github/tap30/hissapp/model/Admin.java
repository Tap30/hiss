package io.github.tap30.hissapp.model;

import io.github.tap30.hiss.EncryptedInside;
import lombok.Data;

import java.util.Map;

@Data
public class Admin extends User {

    @EncryptedInside
    private Map<String, Secret> secrets;

}
