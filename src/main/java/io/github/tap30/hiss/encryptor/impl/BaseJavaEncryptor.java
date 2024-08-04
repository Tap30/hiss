package io.github.tap30.hiss.encryptor.impl;

import io.github.tap30.hiss.encryptor.Encryptor;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Objects;
import java.util.function.Function;

public abstract class BaseJavaEncryptor implements Encryptor {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final String keyAlgorithmName;
    private final String algorithmName;
    private final int ivLength;
    private final Function<byte[], AlgorithmParameterSpec> algorithmParameterSpecSupplier;

    protected BaseJavaEncryptor(String algorithmName,
                                String keyAlgorithmName,
                                int ivLength,
                                Function<byte[], AlgorithmParameterSpec> algorithmParameterSpecSupplier) {
        this.keyAlgorithmName = Objects.requireNonNull(keyAlgorithmName);
        this.algorithmName = Objects.requireNonNull(algorithmName);
        this.ivLength = ivLength;
        this.algorithmParameterSpecSupplier = Objects.requireNonNull(algorithmParameterSpecSupplier);
    }


    @Override
    public byte[] encrypt(byte[] key, byte[] content) throws Exception {
        var secretKeySpec = new SecretKeySpec(key, keyAlgorithmName);
        var cipher = Cipher.getInstance(algorithmName);

        var iv = new byte[ivLength];
        SECURE_RANDOM.nextBytes(iv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, algorithmParameterSpecSupplier.apply(iv));

        var encryptedBytes = cipher.doFinal(content);
        var encryptedIvAndContent = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, encryptedIvAndContent, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, encryptedIvAndContent, iv.length, encryptedBytes.length);

        return encryptedIvAndContent;
    }

    @Override
    public byte[] decrypt(byte[] key, byte[] content) throws Exception {
        var iv = new byte[ivLength];
        var encryptedBytes = new byte[content.length - iv.length];
        System.arraycopy(content, 0, iv, 0, iv.length);
        System.arraycopy(content, iv.length, encryptedBytes, 0, encryptedBytes.length);

        var secretKeySpec = new SecretKeySpec(key, keyAlgorithmName);
        Cipher cipher = Cipher.getInstance(algorithmName);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, algorithmParameterSpecSupplier.apply(iv));

        return cipher.doFinal(encryptedBytes);
    }
}
