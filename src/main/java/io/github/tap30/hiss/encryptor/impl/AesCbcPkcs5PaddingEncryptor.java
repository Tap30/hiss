package io.github.tap30.hiss.encryptor.impl;

import javax.crypto.spec.IvParameterSpec;

public class AesCbcPkcs5PaddingEncryptor extends BaseJavaEncryptor {

    private final static String ALGORITHM_NAME = "AES/CBC/PKCS5Padding";

    public AesCbcPkcs5PaddingEncryptor() {
        super("AES", ALGORITHM_NAME, 16, IvParameterSpec::new);
    }

    @Override
    public String getName() {
        return ALGORITHM_NAME;
    }
}
