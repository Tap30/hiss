package io.github.tap30.hiss;

import io.github.tap30.hiss.hasher.Hasher;
import io.github.tap30.hiss.key.Key;
import io.github.tap30.hiss.utils.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HissHasher {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final Map<String, Hasher> hashers;
    private final Map<String, Key> keys;
    private final String defaultHashingAlgorithm;
    private final String defaultHashingKeyId;

    public HissHasher(Map<String, Hasher> hashers,
                      Map<String, Key> keys,
                      String defaultHashingAlgorithm,
                      String defaultHashingKeyId) {
        this.hashers = Objects.requireNonNull(hashers);
        this.keys = Objects.requireNonNull(keys);
        this.defaultHashingAlgorithm = StringUtils.requireNonBlank(defaultHashingAlgorithm);
        this.defaultHashingKeyId = StringUtils.requireNonBlank(defaultHashingKeyId);
    }

    public String hash(String content, String pattern) throws Exception {
        if (!StringUtils.hasText(content) || HissEncryptor.isHavingEncryptedContentPattern(content)) {
            return content;
        }

        var hasher = Objects.requireNonNull(hashers.get(defaultHashingAlgorithm),
                "Algorithm not supported: " + defaultHashingAlgorithm);
        var key = Objects.requireNonNull(keys.get(defaultHashingKeyId),
                "Key not found: " + defaultHashingKeyId);

        if (StringUtils.hasText(pattern)) {
            StringBuilder result = new StringBuilder();
            Matcher matcher = Pattern.compile(pattern).matcher(content);

            while (matcher.find()) {
                var partToBeEncrypted = matcher.group();
                var hashedContent = hash(hasher, key, partToBeEncrypted);
                matcher.appendReplacement(result, Matcher.quoteReplacement(hashedContent));
            }
            matcher.appendTail(result);

            return result.toString();
        } else {
            return hash(hasher, key, content);
        }
    }

    public boolean isHashed(String content) {
        return HissEncryptor.isHavingEncryptedContentPattern(content);
    }

    private String hash(Hasher hasher, Key key, String content) throws Exception {
        var contentBytes = content.getBytes(CHARSET);
        var hash = hasher.hash(key.getKey(), contentBytes);
        return HissEncryptor.formatEncryptedBytes(hasher.getName(), key.getId(), hash);
    }

}
