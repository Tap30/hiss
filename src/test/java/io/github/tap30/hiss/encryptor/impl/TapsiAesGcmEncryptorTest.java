package io.github.tap30.hiss.encryptor.impl;

import io.github.tap30.hiss.encryptor.BaseEncryptorTest;

class TapsiAesGcmEncryptorTest extends BaseEncryptorTest {

    protected TapsiAesGcmEncryptorTest() {
        super(
                new TapsiAesGcmEncryptor(),
                "aes-128-gcm",
                new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16},
                "2TtYw+dUzrPOPmvgorLoJAWSgXMDbrmz4BvcFA4+wnX1P6661DlbgrI="
        );
    }

}