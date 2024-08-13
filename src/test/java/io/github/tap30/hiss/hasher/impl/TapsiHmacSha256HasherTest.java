package io.github.tap30.hiss.hasher.impl;

import io.github.tap30.hiss.hasher.BaseHasherTest;

class TapsiHmacSha256HasherTest extends BaseHasherTest {

    public TapsiHmacSha256HasherTest() {
        super(
                new TapsiHmacSha256Hasher(),
                "hmac-sha256",
                new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32},
                "ZjSgZLB+ebSU/dD72P6HULVSl6HoRFIEZNoYP9aqIRU="
        );
    }

}