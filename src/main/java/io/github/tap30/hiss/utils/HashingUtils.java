package io.github.tap30.hiss.utils;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HashingUtils {

    public static String hash(String keyId,
                              Map<String, byte[]> keys,
                              String algorithm,
                              String content,
                              String pattern) throws Exception {
        if (!StringUtils.hasText(content) || EncryptionUtils.isHavingEncryptionPattern(content)) {
            return content;
        }
        var key = KeyUtils.getKey(keyId, keys);
        var algorithmSpec = AlgorithmSpec.translateHashingAlgorithm(algorithm);

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithmSpec.getKeyAlgorithmName());

        if (StringUtils.hasText(pattern)) {
            StringBuilder result = new StringBuilder();
            Matcher matcher = Pattern.compile(pattern).matcher(content);

            while (matcher.find()) {
                var partToBeEncrypted = matcher.group();
                var hashedContent = hash(keyId, partToBeEncrypted, algorithmSpec, secretKeySpec);
                matcher.appendReplacement(result, Matcher.quoteReplacement(hashedContent));
            }
            matcher.appendTail(result);

            return result.toString();
        } else {
            return hash(keyId, content, algorithmSpec, secretKeySpec);
        }

    }

    private static String hash(String keyId,
                               String content,
                               AlgorithmSpec algorithmSpec,
                               SecretKeySpec secretKeySpec) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(algorithmSpec.getName());
        mac.init(secretKeySpec);
        byte[] hashedBytes = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return EncryptionUtils.formatEncryptedBytes(algorithmSpec, keyId, hashedBytes);
    }

}
