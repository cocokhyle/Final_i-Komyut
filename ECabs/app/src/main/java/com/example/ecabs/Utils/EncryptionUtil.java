package com.example.ecabs.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.Base64;

import com.example.ecabs.R;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {


    private static final String AES_KEY = "EaTksMhsQmPaLwyW"; //secret key
    private static final String AES_IV = "IgsKysOtbMuYbUlT";   //initialization vector

    public static String encrypt(String data) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKey secretKey = new SecretKeySpec(AES_KEY.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(AES_IV.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        byte[] encryptedBytes = cipher.doFinal(data.getBytes("UTF-8"));
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    public static String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKey secretKey = new SecretKeySpec(AES_KEY.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(AES_IV.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        byte[] decryptedBytes = cipher.doFinal(Base64.decode(encryptedData, Base64.DEFAULT));
        return new String(decryptedBytes, "UTF-8");
    }
}
