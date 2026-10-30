package com.preesa.razorpay.vault_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import javax.crypto.spec.SecretKeySpec;

@Configuration
public class VaultEncryptionConfig {

    @Value("${vault.master-key}")
    private String masterKey;

    public static BytesEncryptor panEncryptor(byte[] dek){
        SecretKeySpec dekByte= new SecretKeySpec(dek,"AES");
        return new AesBytesEncryptor(dekByte, KeyGenerators.secureRandom(12), AesBytesEncryptor.CipherAlgorithm.GCM);
    }


}
