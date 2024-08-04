package io.github.tap30.hiss.encryptor.impl;

import javax.crypto.spec.IvParameterSpec;

public class AesCbcPkcs5PaddingEncryptor extends BaseJavaEncryptor {

    private final static String ALGORITHM_NAME = "AES/CBC/PKCS5Padding";

    public AesCbcPkcs5PaddingEncryptor() {
        super(ALGORITHM_NAME, "AES", 16, IvParameterSpec::new);
    }

    @Override
    public String getName() {
        return ALGORITHM_NAME;
    }
}
