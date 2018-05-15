package com.prodev.encyclopedia.tools;

import android.util.Base64;

import javax.crypto.spec.*;
import java.security.*;
import java.util.*;
import javax.crypto.*;

public class Crypter
{
    public static SecretKeySpec createKey(String strKey) throws Exception {
        try {
            byte[] key = strKey.getBytes("UTF-8");

            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);

            return new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public static String encrypt(String strText, String strKey) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, createKey(strKey));

            byte[] encrypted = cipher.doFinal(strText.getBytes("UTF-8"));
            return new String(Base64.encodeToString(encrypted, Base64.DEFAULT));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public static String decrypt(String strEncrypted, String strKey) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, createKey(strKey));

            byte[] decrypted = cipher.doFinal(Base64.decode(strEncrypted.getBytes("UTF-8"), Base64.DEFAULT));
            return new String(decrypted);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}