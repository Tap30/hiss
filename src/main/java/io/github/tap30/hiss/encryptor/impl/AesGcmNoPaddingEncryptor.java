package io.github.tap30.hiss.encryptor.impl;

import javax.crypto.spec.GCMParameterSpec;

public class AesGcmNoPaddingEncryptor extends BaseJavaEncryptor {

    private static final String ALGORITHM_NAME = "AES/GCM/NoPadding";

    public AesGcmNoPaddingEncryptor() {
        super(ALGORITHM_NAME, "AES", 16, iv -> new GCMParameterSpec(128, iv));
    }

    @Override
    public String getName() {
        return ALGORITHM_NAME;
    }

}
