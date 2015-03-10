package com.linxinzhe.android.xinzhesecurity.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Tools {

    /**
     * MD5加密
     *
     * @param password 明文
     * @return 密文
     */
    public static String encrypt(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(password.getBytes());
            StringBuilder builder = new StringBuilder();
            for (byte b : result) {
                int number = b & 0xff;
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    builder.append("0");
                }
                builder.append(str);
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
