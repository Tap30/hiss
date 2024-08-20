package io.github.tap30.hiss.encryptor.impl;

public class TapsiAesCbcEncryptor extends AesCbcPkcs5PaddingEncryptor {
    @Override
    public String getName() {
        return "aes-128-cbc";
    }
}
