package io.github.tap30.hiss.encryptor;

public class TapsiAesGcmEncryptor extends AesGcmNoPaddingEncryptor {
    @Override
    public String getName() {
        return "aes-128-gcm";
    }
}
