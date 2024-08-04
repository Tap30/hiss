package io.github.tap30.hiss.hasher;

public interface Hasher {
    /**
     * Calculates hash of provided content.
     * @param key
     * @param content
     * @return hash of content.
     */
    byte[] hash(byte[] key, byte[] content) throws Exception;

    /**
     * Returns the hasher name which will be put in final hashed content.
     * <br>
     * The name <b>must not</b> contain
     * <code>'{'</code>, <code>'}'</code>, <code>':'</code>, and <code>'#$$#'</code>.
     * @return hasher name.
     */
    String getName(); // todo: should names be lowercased?
}
