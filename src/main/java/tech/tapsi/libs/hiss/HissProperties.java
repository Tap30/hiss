package tech.tapsi.libs.hiss;

import lombok.Value;
import tech.tapsi.libs.hiss.utils.KeyUtils;
import tech.tapsi.libs.hiss.utils.StringUtils;

import java.util.ArrayList;
import java.util.Map;

@Value
public class HissProperties {

    Map<String, byte[]> keys;
    String defaultEncryptionKeyId;
    String defaultEncryptionAlgorithm;
    String defaultHashingKeyId;
    String defaultHashingAlgorithm;

    public static HissProperties fromBase64EncodedKeys(Map<String, String> keys,
                                                       String defaultEncryptionKeyId,
                                                       String defaultEncryptionAlgorithm,
                                                       String defaultHashingKeyId,
                                                       String defaultHashingAlgorithm) {
        return new HissProperties(
                KeyUtils.convertBase64KeysToByteArrayKeys(keys),
                defaultEncryptionKeyId,
                defaultEncryptionAlgorithm,
                defaultHashingKeyId,
                defaultHashingAlgorithm
        );
    }

    public void validate() {
        var errors = new ArrayList<String>();
        if (this.keys == null || this.keys.isEmpty()) {
            errors.add("Keys are empty");
        } else {
            this.keys.forEach((k, v) -> {
                if (v == null || v.length == 0) {
                    errors.add("Key " + k + " is empty");
                }
            });
        }
        if (!StringUtils.hasText(this.defaultEncryptionKeyId)) {
            errors.add("Default encryption key ID is not defined");
        }
        if (!StringUtils.hasText(this.defaultEncryptionAlgorithm)) {
            errors.add("Default encryption algorithm is not defined");
        }
        if (!StringUtils.hasText(this.defaultHashingKeyId)) {
            errors.add("Default hashing key ID is not defined");
        }
        if (!StringUtils.hasText(this.defaultHashingAlgorithm)) {
            errors.add("Default hashing algorithm is not defined");
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Hiss properties are not valid: " + String.join("; ", errors));
        }
    }

}
