package com.novarto.lang.crypto;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * @author bobbymicroby
 */
public class PasswordHasher
{
    private final int saltSize;
    private final int hashSize;
    private final int hashStrechIterations;

    /**
     * More then 24 bytes for hash and 24 bytes for salt is unreasonable As well as using more then 1000 key streches
     *
     * @param saltSize             size of the salt in bytes
     * @param hashSize             size of the hash in bytes
     * @param hashStrechIterations number of key strech iterations iterations check
     *                             <a href="https://en.wikipedia.org/wiki/Key_stretching">https://en.wikipedia
     *                             .org/wiki/Key_stretching</a>
     */
    public PasswordHasher(int saltSize, int hashSize, int hashStrechIterations)
    {
        this.saltSize = saltSize;
        this.hashSize = hashSize;
        this.hashStrechIterations = hashStrechIterations;
    }

    public boolean validatePassword(String password, String correctHash)
    {
        String[] params = correctHash.split(":");
        byte[] hash = fromHex(params[0]);
        byte[] salt = fromHex(params[1]);
        byte[] testHash = hash(password, salt);
        // Compare the hashes in constant time. The password is correct if
        // both hashes match.
        return timingAttackSaveEquals(hash, testHash);
    }

    /**
     * @param a
     * @param b
     * @return
     * @see <a href="https://en.wikipedia.org/wiki/Timing_attack">https://en.wikipedia.org/wiki/Timing_attack</a>
     */
    private boolean timingAttackSaveEquals(byte[] a, byte[] b)
    {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++)
        {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }

    /**
     * Hash a password with generated salt
     *
     * @param password
     * @return
     */
    public String toHash(String password)
    {
        return toHash(password, generateSalt());
    }

    /**
     * Hash a password with user provided salt
     *
     * @param password
     * @param salt
     * @return
     */
    public String toHash(String password, byte[] salt)
    {
        StringBuilder b = new StringBuilder();
        final byte[] hash = hash(password, salt);
        b.append(toHex(hash));
        b.append(":");
        b.append(toHex(salt));
        return b.toString();
    }

    /**
     * Bytes to hex string
     *
     * @param bytes
     * @return
     */
    private String toHex(byte[] bytes)
    {
        return DatatypeConverter.printHexBinary(bytes);
    }

    /**
     * Hex to bytes
     *
     * @param hex
     * @return
     */
    private static byte[] fromHex(String hex)
    {
        return DatatypeConverter.parseHexBinary(hex);
    }

    /**
     * Hash a password with user provided salt
     *
     * @param password
     * @param salt
     * @return
     */
    private byte[] hash(String password, byte[] salt)
    {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, hashStrechIterations, hashSize * 8);
        SecretKeyFactory skf;
        try
        {
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
        try
        {
            return skf.generateSecret(spec).getEncoded();
        }
        catch (InvalidKeySpecException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate random salt
     *
     * @return
     */
    private byte[] generateSalt()
    {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[saltSize];
        random.nextBytes(salt);
        return salt;
    }
}