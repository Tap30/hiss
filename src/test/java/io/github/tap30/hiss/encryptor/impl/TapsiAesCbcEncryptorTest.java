package io.github.tap30.hiss.encryptor.impl;

import io.github.tap30.hiss.encryptor.BaseEncryptorTest;

class TapsiAesCbcEncryptorTest extends BaseEncryptorTest {

    protected TapsiAesCbcEncryptorTest() {
        super(
                new TapsiAesCbcEncryptor(),
                "aes-128-cbc",
                new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16},
                "bzoCDPV5ddz6GEOm1PRt4V9nQJs4Dc6xRFcMea5xB9I="
        );
    }

}