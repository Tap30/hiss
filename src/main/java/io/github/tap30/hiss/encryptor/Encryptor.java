package io.github.tap30.hiss.encryptor;

public interface Encryptor {
    /**
     * Encrypts content using key.
     * @param key
     * @param content
     * @return encrypted content.
     */
    byte[] encrypt(byte[] key, byte[] content) throws Exception;

    /**
     * Decrypts content using key
     * @param key
     * @param content
     * @return plain content.
     */
    byte[] decrypt(byte[] key, byte[] content) throws Exception;

    /**
     * Returns the encryptor name which will be put in final encrypted content.
     * <br>
     * The name <b>must not</b> contain
     * <code>'{'</code>, <code>'}'</code>, <code>':'</code>, and <code>'#$$#'</code>.
     * @return encryptor name.
     */
    String getName(); // todo: names should be lowercased?
}
