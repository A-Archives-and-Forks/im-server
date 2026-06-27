package io.moquette.spi.security;

import io.netty.util.internal.StringUtil;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class DES {
	private static String Encrypt_Password = "abcdefgh";
    private static byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8 };

    public static void setEncryptPassword(String encrypt_Password) {
        if(!StringUtil.isNullOrEmpty(encrypt_Password)) {
            if(encrypt_Password.length() < 8) {
                encrypt_Password = encrypt_Password+Encrypt_Password;
            }
            if(encrypt_Password.length() > 8) {
                encrypt_Password = encrypt_Password.substring(0, 8);
            }
            Encrypt_Password = encrypt_Password;
        }
    }

    public static String decryptDES(String decryptString) throws Exception {
        byte[] byteMi = Base64.getDecoder().decode(decryptString);
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(Encrypt_Password.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        byte decryptedData[] = cipher.doFinal(byteMi);

        return new String(decryptedData);
    }

    public static String encryptDES(String encryptString) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(Encrypt_Password.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
        return new String(Base64.getEncoder().encode(encryptedData));
    }
}
