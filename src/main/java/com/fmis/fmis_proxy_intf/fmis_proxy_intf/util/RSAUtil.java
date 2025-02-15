package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
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
     * Decrypts an encrypted string using an RSA private key.
     *
     * @param publicKeyString The Base64 encoded encrypted data.
     * @param privateKeyString The Base64 encoded RSA private key.
     * @return A map containing the decrypted data or error message.
     */
    public static Map<String, Object> decrypt(String publicKeyString, String privateKeyString) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Convert the private key string to a PrivateKey object
            PrivateKey privateKey = loadPrivateKey(privateKeyString);

            // Initialize the cipher for decryption
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            // Decode the encrypted data from Base64 to binary format
            byte[] encryptedBytes = Base64.getDecoder().decode(publicKeyString);

            // Decrypt the data
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // Store the decrypted data in the result map
            result.put("decrypt", new String(decryptedBytes, StandardCharsets.UTF_8));

        } catch (Exception e) {
            // In case of error, store the error details in the result map
            result.put("code", "500");
            result.put("message", "Decryption failed.");
            result.put("error", e.getMessage());
        }

        return result;
    }


    /**
     * Generates an RSA key pair.
     *
     * @return A map containing the public and private keys as Base64-encoded strings.
     */
    public static Map<String, Object> RSAKeyPairGenerator() {
        Map<String, Object> key = new HashMap<>();
        try {
            // Generate RSA Key Pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Encode keys into Base64 strings
            key.put("public_key", Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
            key.put("private_key", Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating RSA key pair", e);
        }
        return key;
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

    /**
     * Generates a key set including Base64 encoding, SHA-256 hash, and RSA key pair.
     *
     * @param value The input value to be encoded and encrypted.
     * @return A map containing the generated keys.
     */
    public static Map<String, Object> generatePartnerKey(String value) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Generate Base64 encoding
            byte[] base64Bytes = Base64.getEncoder().encode(value.getBytes(StandardCharsets.UTF_8));

            // Compute SHA-256 hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] sha256Bytes = digest.digest(base64Bytes);

            // Generate RSA key pair
            Map<String, Object> key = RSAKeyPairGenerator();
            String publicKeyString = key.get("public_key").toString();
            String privateKeyString = key.get("private_key").toString();

            // Encrypt value using RSA public key
            PublicKey publicKey = convertStringToPublicKey(publicKeyString);
            String encryptedValue = encrypt(value, publicKey);

            response.put("code", value);
            response.put("base64", Base64.getEncoder().encodeToString(base64Bytes));
            response.put("sha256", Base64.getEncoder().encodeToString(sha256Bytes));
            response.put("public_key", encryptedValue);
            response.put("private_key", privateKeyString);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "An unexpected error occurred.");
            response.put("error", e.getMessage());
        }
        return response;
    }
}
