package io.github.tap30.hiss;

import io.github.tap30.hiss.encryptor.Encryptor;
import io.github.tap30.hiss.key.Key;
import io.github.tap30.hiss.utils.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    public HissEncryptor(Set<Encryptor> encryptors,
                         Map<String, Key> keys,
                         String defaultEncryptionAlgorithm,
                         String defaultEncryptionKeyId) {
        Objects.requireNonNull(encryptors);
        this.encryptors = encryptors.stream().collect(Collectors.toMap(e -> e.getName().toLowerCase(), e -> e));
        this.keys = Objects.requireNonNull(keys);
        this.defaultEncryptionAlgorithm = StringUtils.requireNonBlank(defaultEncryptionAlgorithm);
        this.defaultEncryptionKeyId = StringUtils.requireNonBlank(defaultEncryptionKeyId);
        // todo: validate defaults are found in encryptors and keys
    }

    public String encrypt(String content, String pattern) throws Exception {
        if (!StringUtils.hasText(content) || isEncrypted(content)) {
            return content;
        }

        var encryptor = encryptors.get(defaultEncryptionAlgorithm); // todo: not null
        var key = keys.get(defaultEncryptionKeyId); // todo: not null

        if (StringUtils.hasText(pattern)) {
            StringBuilder result = new StringBuilder();
            Matcher matcher = Pattern.compile(pattern).matcher(content);

            while (matcher.find()) {
                var partToBeEncrypted = matcher.group();
                var encryptedContent = encrypt(encryptor, key, partToBeEncrypted);
                matcher.appendReplacement(result, Matcher.quoteReplacement(encryptedContent));
            }
            matcher.appendTail(result);

            return result.toString();
        } else {
            return encrypt(encryptor, key, content);
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

            var key = keys.get(keyId); // todo: not null
            var encryptor = encryptors.get(algorithm); // todo: not null

            var decryptedContent = decrypt(encryptor, key, encryptedContent);
            matcher.appendReplacement(result, Matcher.quoteReplacement(decryptedContent));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public boolean isEncrypted(String content) {
        return isHavingEncryptedContentPattern(content);
    }

    private String encrypt(Encryptor encryptor, Key key, String content) throws Exception {
        var contentBytes = content.getBytes(CHARSET);
        var encryptedBytes = encryptor.encrypt(key.getKey(), contentBytes);
        return formatEncryptedBytes(encryptor.getName(), key.getId(), encryptedBytes);
    }

    private String decrypt(Encryptor encryptor, Key key, String content) throws Exception {
        var contentBytes = Base64.getDecoder().decode(content);
        var decryptedBytes = encryptor.decrypt(key.getKey(), contentBytes);
        return new String(decryptedBytes, CHARSET);
    }


    public static String formatEncryptedBytes(String algorithmName, String keyId, byte[] bytes) {
        var base64Encoded = Base64.getEncoder().encodeToString(bytes);
        return "#$$#{" + algorithmName + ":" + keyId + "}{" + base64Encoded + "}#$$#";
    }

    public static boolean isHavingEncryptedContentPattern(String content) {
        return ENCTYPTED_CONTENT_PATTERN.matcher(content).find();
    }

}
