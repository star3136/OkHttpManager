package com.askew.net.utils;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by lihoudong204 on 2018/10/30
 */
public class MD5Utils {
    public static String md5(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(text.getBytes("UTF-8"));
            byte[] bytes = digest.digest();
            BigInteger bi = new BigInteger(1, bytes);
            return bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
