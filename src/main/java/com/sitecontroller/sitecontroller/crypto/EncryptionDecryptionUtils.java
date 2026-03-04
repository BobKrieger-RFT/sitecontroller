package com.sitecontroller.sitecontroller.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.sitecontroller.sitecontroller.common.util.HexUtils;

public final class EncryptionDecryptionUtils {

    private static byte[] key = HexUtils.convertFromHexString("4A87ED4119C8E8BF30CC23BD66B182CD");
    private static byte[] iv = HexUtils.convertFromHexString("D5BD25E7818DA98D8027896211B91B91");
    private static SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

    public static String encrypt(final String s) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
        byte[] encryptedBytes = cipher.doFinal(s.getBytes("UTF-8"));
        return HexUtils.convertToHexString(encryptedBytes);
    }

    public static String decrypt(final String s) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
        return new String(cipher.doFinal(HexUtils.convertFromHexString(s)), "UTF-8");
    }
}
