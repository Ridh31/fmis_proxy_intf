package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.RSAUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for managing partner-related operations.
 */
@RestController
@RequestMapping("/api/partner")
public class PartnerController {

    private final PartnerService partnerService;

    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    /**
     * Endpoint to create a partner.
     *
     * @param partner The partner details.
     * @return Response entity with the creation status.
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Object>> createPartner(@RequestBody Partner partner) {
        try {
            // Generate key from partner code
            Map<String, Object> key = generatePartnerKey(partner.getCode());

            // Set generated key values in the partner entity
            partner.setBase64(key.get("base64").toString());
            partner.setSha256(key.get("sha256").toString());
            partner.setRsaPublicKey(key.get("public_key").toString());
            partner.setRsaPrivateKey(key.get("private_key").toString());

            // Check if partner code already exists
            if (partnerService.findByCode(partner.getCode()).isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                "400",
                                "Partner's code already taken. Please use another code!"
                        ));
            }

            // Save the partner and return response
            Partner savedPartner = partnerService.createPartner(partner);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse<>(
                            "200",
                            "Partner created successfully!",
                            savedPartner
                    ));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            "500",
                            e.getMessage()
                    ));
        }
    }

    /**
     * Generates a key set including Base64 encoding, SHA-256 hash, and RSA key pair.
     *
     * @param value The input value to be encoded and encrypted.
     * @return A map containing the generated keys.
     */
    // @GetMapping("/generate-key")
    public Map<String, Object> generatePartnerKey(@RequestParam String value) {
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
            PublicKey publicKey = RSAUtil.convertStringToPublicKey(publicKeyString);
            String encryptedValue = RSAUtil.encrypt(value, publicKey);

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

    /**
     * Decrypts an encrypted string using an RSA private key.
     *
     * @param requestBody JSON containing "encryptedData" and "privateKeyString".
     * @return A map containing the decrypted data.
     */
    // @PostMapping("/decrypt")
    public Map<String, Object> decrypt(@RequestBody Map<String, Object> requestBody) {
        Map<String, Object> result = new HashMap<>();

        try {
            String encryptedData = requestBody.get("encryptedData").toString();
            String privateKeyString = requestBody.get("privateKeySting").toString();

            // Convert the private key string to a PrivateKey object
            PrivateKey privateKey = RSAUtil.loadPrivateKey(privateKeyString);

            // Initialize the cipher for decryption
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            // Decode Base64 back to binary format
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);

            // Decode and decrypt
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            result.put("decrypt", new String(decryptedBytes, StandardCharsets.UTF_8));

        } catch (Exception e) {
            result.put("status", "error");
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
    // @GetMapping("/generate-key-pair")
    public Map<String, Object> RSAKeyPairGenerator() {
        Map<String, Object> key = new HashMap<>();
        try {
            // Generate RSA Key Pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
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
}
