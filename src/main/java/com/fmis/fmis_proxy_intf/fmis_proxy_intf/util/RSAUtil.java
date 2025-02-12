package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import javax.crypto.Cipher;

/**
 * Utility class for RSA encryption, decryption, and key conversion.
 */
public class RSAUtil {

    private static final String RSA = "RSA";

    /**
     * Encrypts the given data using the provided RSA public key.
     *
     * @param data      The plaintext data to be encrypted.
     * @param publicKey The public key used for encryption.
     * @return The encrypted data encoded in Base64 format.
     * @throws Exception if encryption fails.
     */
    public static String encrypt(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    /**
     * Decrypts the given Base64-encoded encrypted data using the provided RSA private key.
     *
     * @param encryptedData The encrypted data in Base64 format.
     * @param privateKey    The private key used for decryption.
     * @return The decrypted plaintext data.
     * @throws Exception if decryption fails.
     */
    public static String decrypt(String encryptedData, PrivateKey privateKey) throws Exception {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    /**
     * Loads an RSA private key from a Base64-encoded string.
     *
     * @param privateKeyString The private key in PEM format as a string.
     * @return The {@link PrivateKey} object.
     * @throws Exception if the key cannot be parsed.
     */
    public static PrivateKey loadPrivateKey(String privateKeyString) throws Exception {
        String cleanedKey = privateKeyString.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decodedKey = Base64.getDecoder().decode(cleanedKey);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * Converts a Base64-encoded PEM formatted RSA public key string into a {@link PublicKey} object.
     *
     * @param publicKeyPEM The public key in PEM format as a string.
     * @return The {@link PublicKey} object.
     * @throws Exception if the key cannot be parsed.
     */
    public static PublicKey convertStringToPublicKey(String publicKeyPEM) throws Exception {
        String cleanedKey = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decodedKey = Base64.getDecoder().decode(cleanedKey);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        return keyFactory.generatePublic(keySpec);
    }
}
