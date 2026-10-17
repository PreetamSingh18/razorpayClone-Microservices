package com.preesa.razorpay.common_lib.config;

import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


public class AesEncryptionConfig {


    public  BytesEncryptor masterKeyEncryptor(String masterKey){
         byte[] masterKeyByte= Base64.getDecoder().decode(masterKey);
          SecretKeySpec dekByte= new SecretKeySpec(masterKeyByte,"AES/GCM/NoPadding");
        return new AesBytesEncryptor(dekByte, KeyGenerators.secureRandom(12), AesBytesEncryptor.CipherAlgorithm.GCM);
    }
}
