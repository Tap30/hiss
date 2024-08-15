package io.github.tap30.hiss;

import io.github.tap30.hiss.encryptor.Encryptor;
import io.github.tap30.hiss.key.Key;
import io.github.tap30.hiss.utils.StringUtils;
import lombok.Value;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HissEncryptor {

    /**
     * Group 1 is algorithm name; group 2 is key ID; group 3 is base64 encoded encrypted content
     * Example: #$$#{aes-128-gcm:default_key}{P4KYuz1zmvJC+vDIR4ej9bKX+e2uAapg040b1cLYxtjBx9RShwUbRFpUcQ==}#$$#
     */
    private static final Pattern ENCTYPTED_CONTENT_PATTERN = Pattern.compile("#\\$\\$#\\{(.*?):(.*?)}\\{(.+?)}#\\$\\$#");
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final Map<String, Encryptor> encryptors;
    private final Map<String, Key> keys;
    private final String defaultEncryptionAlgorithm;
    private final String defaultEncryptionKeyId;

    public HissEncryptor(Map<String, Encryptor> encryptors,
                         Map<String, Key> keys,
                         String defaultEncryptionAlgorithm,
                         String defaultEncryptionKeyId) {
        this.encryptors = Objects.requireNonNull(encryptors);
        this.keys = Objects.requireNonNull(keys);
        this.defaultEncryptionAlgorithm = StringUtils.requireNonBlank(defaultEncryptionAlgorithm);
        this.defaultEncryptionKeyId = StringUtils.requireNonBlank(defaultEncryptionKeyId);
    }

    public String encrypt(String content, String pattern) throws Exception {
        if (!StringUtils.hasText(content) || isEncrypted(content)) {
            return content;
        }

        var encryptorAndKey = getEncryptorAndKey(defaultEncryptionAlgorithm, defaultEncryptionKeyId);

        if (StringUtils.hasText(pattern)) {
            StringBuilder result = new StringBuilder();
            Matcher matcher = Pattern.compile(pattern).matcher(content);

            while (matcher.find()) {
                var partToBeEncrypted = matcher.group();
                var encryptedContent = encrypt(encryptorAndKey, partToBeEncrypted);
                matcher.appendReplacement(result, Matcher.quoteReplacement(encryptedContent));
            }
            matcher.appendTail(result);

            return result.toString();
        } else {
            return encrypt(encryptorAndKey, content);
        }
    }

    public String decrypt(String content) throws Exception {
        if (!StringUtils.hasText(content) || !isEncrypted(content)) {
            return content;
        }

        var result = new StringBuilder();

        var matcher = ENCTYPTED_CONTENT_PATTERN.matcher(content);
        while (matcher.find()) {
            var algorithm = matcher.group(1);
            var keyId = matcher.group(2);
            var encryptedContent = matcher.group(3);

            var decryptedContent = decrypt(getEncryptorAndKey(algorithm, keyId), encryptedContent);
            matcher.appendReplacement(result, Matcher.quoteReplacement(decryptedContent));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public boolean isEncrypted(String content) {
        return isHavingEncryptedContentPattern(content);
    }

    public static String formatEncryptedBytes(String algorithmName, String keyId, byte[] bytes) {
        var base64Encoded = Base64.getEncoder().encodeToString(bytes);
        return "#$$#{" + algorithmName + ":" + keyId + "}{" + base64Encoded + "}#$$#";
    }

    public static boolean isHavingEncryptedContentPattern(String content) {
        return ENCTYPTED_CONTENT_PATTERN.matcher(content).find();
    }

    private String encrypt(EncryptorAndKey encryptorAndKey, String content) throws Exception {
        var encryptor = encryptorAndKey.getEncryptor();
        var key = encryptorAndKey.getKey();

        var contentBytes = content.getBytes(CHARSET);
        var encryptedBytes = encryptor.encrypt(key.getKey(), contentBytes);
        return formatEncryptedBytes(encryptor.getName(), key.getId(), encryptedBytes);
    }

    private String decrypt(EncryptorAndKey encryptorAndKey, String content) throws Exception {
        var encryptor = encryptorAndKey.getEncryptor();
        var key = encryptorAndKey.getKey();

        var contentBytes = Base64.getDecoder().decode(content);
        var decryptedBytes = encryptor.decrypt(key.getKey(), contentBytes);
        return new String(decryptedBytes, CHARSET);
    }

    private EncryptorAndKey getEncryptorAndKey(String algorithmName, String keyId) {
        var encryptor = Objects.requireNonNull(encryptors.get(algorithmName), "Algorithm not supported: " + algorithmName);
        var key = Objects.requireNonNull(keys.get(keyId), "Key not found: " + keyId);
        return new EncryptorAndKey(encryptor, key);
    }

    @Value
    private static class EncryptorAndKey {
        Encryptor encryptor;
        Key key;
    }

}
