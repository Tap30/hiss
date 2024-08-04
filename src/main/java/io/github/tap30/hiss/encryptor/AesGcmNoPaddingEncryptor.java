package io.github.tap30.hiss.encryptor;

import javax.crypto.spec.GCMParameterSpec;

public class AesGcmNoPaddingEncryptor extends BaseJavaEncryptor {

    private static final String ALGORITHM_NAME = "AES/GCM/NoPadding";

    public AesGcmNoPaddingEncryptor() {
        super("AES", ALGORITHM_NAME, 16, iv -> new GCMParameterSpec(128, iv));
    }

    @Override
    public String getName() {
        return ALGORITHM_NAME;
    }

}
