package tech.tapsi.libs.hiss.utils;

import lombok.Value;

import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.util.function.Function;

@Value
public class AlgorithmSpec {

    String originalName;
    String name;
    String keyAlgorithmName;
    int ivLength;
    Function<byte[], AlgorithmParameterSpec> algorithmParameterSpec;


    public AlgorithmParameterSpec createAlgorithmParameterSpec(byte[] iv) {
        return this.algorithmParameterSpec.apply(iv);
    }

    static AlgorithmSpec translateEncryptionAlgorithm(String algorithm) {
        AlgorithmSpec algorithmSpec;
        switch (algorithm.toLowerCase()) {
            default:
                throw new IllegalArgumentException("Encryption algorithm " + algorithm + " not supported");
            case "aes-128-gcm":
                algorithmSpec = new AlgorithmSpec(algorithm, "AES/GCM/NoPadding", "AES", 16,
                        iv -> new GCMParameterSpec(128, iv));
                break;
            case "aes-128-cbc":
                algorithmSpec = new AlgorithmSpec(algorithm, "AES/CBC/PKCS5Padding", "AES", 16,
                        IvParameterSpec::new);
                break;
        }
        return algorithmSpec;
    }

    static AlgorithmSpec translateHashingAlgorithm(String algorithm) {
        AlgorithmSpec algorithmSpec;
        switch (algorithm.toLowerCase()) {
            default:
                throw new IllegalArgumentException("Hashing algorithm " + algorithm + " not supported");
            case "hmac-sha256":
                algorithmSpec = new AlgorithmSpec(algorithm, "HmacSHA256", "HmacSHA256", 0, null);
                break;
        }
        return algorithmSpec;
    }
}
