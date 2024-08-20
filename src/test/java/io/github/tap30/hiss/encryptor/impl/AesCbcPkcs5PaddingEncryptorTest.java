package io.github.tap30.hiss.encryptor.impl;

import io.github.tap30.hiss.encryptor.BaseEncryptorTest;

class AesCbcPkcs5PaddingEncryptorTest extends BaseEncryptorTest {

    protected AesCbcPkcs5PaddingEncryptorTest() {
        super(
                new AesCbcPkcs5PaddingEncryptor(),
                "AES/CBC/PKCS5Padding",
                new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16},
                "bzoCDPV5ddz6GEOm1PRt4V9nQJs4Dc6xRFcMea5xB9I="
        );
    }

}