package org.acgnu.third.oplus;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class OPlusMD5 {
    public static String getMD5(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(str.getBytes());
            return getHashString(instance);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getMD5_16(String str) {
        String md5 = getMD5(str);
        if (md5 == null) {
            return null;
        }
        try {
            return md5.substring(8, 24);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getHashString(MessageDigest messageDigest) {
        StringBuilder sb = new StringBuilder();
        for (byte b : messageDigest.digest()) {
            sb.append(Integer.toHexString((b >> 4) & 15));
            sb.append(Integer.toHexString(b & (byte) 15));
        }
        return sb.toString();
    }
}
