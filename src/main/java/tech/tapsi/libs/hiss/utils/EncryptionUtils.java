package tech.tapsi.libs.hiss.utils;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EncryptionUtils {

    /**
     * Group 1 is algorithm name; group 2 is key ID; group 3 is base64 encoded encrypted content
     * Example: #$$#{aes-128-gcm:default_key}{P4KYuz1zmvJC+vDIR4ej9bKX+e2uAapg040b1cLYxtjBx9RShwUbRFpUcQ==}#$$#
      */
    static final Pattern ENCTYPTED_CONTENT_PATTERN = Pattern.compile("#\\$\\$#\\{(.*?):(.*?)}\\{(.+?)}#\\$\\$#");

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String encrypt(String keyId,
                                 Map<String, byte[]> keys,
                                 String algorithm,
                                 String content,
                                 String pattern) throws Exception {
        if (!StringUtils.hasText(content) || isHavingEncryptionPattern(content)) {
            return content;
        }

        var key = KeyUtils.getKey(keyId, keys);
        var algorithmSpec = AlgorithmSpec.translateEncryptionAlgorithm(algorithm);

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithmSpec.getKeyAlgorithmName());
        Cipher cipher = Cipher.getInstance(algorithmSpec.getName());

        if (StringUtils.hasText(pattern)) {
            StringBuilder result = new StringBuilder();
            Matcher matcher = Pattern.compile(pattern).matcher(content);

            while (matcher.find()) {
                var partToBeEncrypted = matcher.group();
                var encryptedContent = encrypt(keyId, algorithmSpec, cipher, secretKeySpec, partToBeEncrypted);
                matcher.appendReplacement(result, Matcher.quoteReplacement(encryptedContent));
            }
            matcher.appendTail(result);

            return result.toString();
        } else {
            return encrypt(keyId, algorithmSpec, cipher, secretKeySpec, content);
        }
    }

    private static String encrypt(String keyId,
                                  AlgorithmSpec algorithmSpec,
                                  Cipher cipher,
                                  SecretKeySpec secretKeySpec,
                                  String content) throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] iv = new byte[algorithmSpec.getIvLength()];
        SECURE_RANDOM.nextBytes(iv);
        AlgorithmParameterSpec algorithmParameterSpec = algorithmSpec.createAlgorithmParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, algorithmParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        byte[] encryptedIvAndContent = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, encryptedIvAndContent, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, encryptedIvAndContent, iv.length, encryptedBytes.length);

        return formatEncryptedBytes(algorithmSpec, keyId, encryptedIvAndContent);
    }


    public static String decrypt(Map<String, byte[]> keys, String content) throws Exception {
        if (!StringUtils.hasText(content) || !isHavingEncryptionPattern(content)) {
            return content;
        }

        var result = new StringBuilder();

        var matcher = ENCTYPTED_CONTENT_PATTERN.matcher(content);
        while (matcher.find()) {
            var algorithm = matcher.group(1);
            var keyId = matcher.group(2);
            var encryptedContent = matcher.group(3);

            var key = KeyUtils.getKey(keyId, keys);
            var algorithmSpec = AlgorithmSpec.translateEncryptionAlgorithm(algorithm);

            var decryptedContent = decrypt(encryptedContent, algorithmSpec, key);
            matcher.appendReplacement(result, Matcher.quoteReplacement(decryptedContent));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private static String decrypt(String encryptedContent,
                                  AlgorithmSpec algorithmSpec,
                                  byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] ivAndEncryptedContent = Base64.getDecoder().decode(encryptedContent);
        byte[] iv = new byte[algorithmSpec.getIvLength()];
        byte[] encryptedBytes = new byte[ivAndEncryptedContent.length - iv.length];
        System.arraycopy(ivAndEncryptedContent, 0, iv, 0, iv.length);
        System.arraycopy(ivAndEncryptedContent, iv.length, encryptedBytes, 0, encryptedBytes.length);

        AlgorithmParameterSpec algorithmParameterSpec = algorithmSpec.createAlgorithmParameterSpec(iv);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithmSpec.getKeyAlgorithmName());

        Cipher cipher = Cipher.getInstance(algorithmSpec.getName());
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, algorithmParameterSpec);

        return new String(cipher.doFinal(encryptedBytes), StandardCharsets.UTF_8);
    }

    public static boolean isHavingEncryptionPattern(String content) {
        return ENCTYPTED_CONTENT_PATTERN.matcher(content).find();
    }

    static String formatEncryptedBytes(AlgorithmSpec algorithmSpec, String keyId, byte[] bytes) {
        var base64Encoded = Base64.getEncoder().encodeToString(bytes);
        return "#$$#{" + algorithmSpec.getOriginalName() + ":" + keyId + "}{" + base64Encoded + "}#$$#";
    }

}
