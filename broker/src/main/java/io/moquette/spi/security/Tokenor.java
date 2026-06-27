package io.moquette.spi.security;


import com.hazelcast.util.StringUtil;
import io.moquette.spi.impl.security.AES;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;

public class Tokenor {
	private static String KEY = "testim";
	private static long expiredTime = Long.MAX_VALUE;
    private static final SecureRandom RANDOM = new SecureRandom();
	public static void setKey(String key) {
	    if (!StringUtil.isNullOrEmpty(key)) {
            KEY = key;
        }
    }

    public static void setExpiredTime(long expiredTime) {
        Tokenor.expiredTime = expiredTime;
    }

    public static String getUserId(byte[] password) {
        try {
            String signKey =
                DES.decryptDES(new String(password));

            if (signKey.startsWith(KEY + "|")) {
                signKey = signKey.substring(KEY.length() + 1);
                long timestamp = Long.parseLong(signKey.substring(0, signKey.indexOf('|')));
                if (expiredTime > 0 && System.currentTimeMillis() - timestamp > expiredTime) {
                    return null;
                }
                String id = signKey.substring(signKey.indexOf('|') + 1);
                if(id.contains("|")) {
                    id = id.substring(0, id.indexOf("|"));
                }
                return id;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        return null;
    }
    public static String getToken(String username) {
        String signKey = KEY + "|" + (System.currentTimeMillis()) + "|" + username + "|" + getRadomString(4);
        try {
            return DES.encryptDES(signKey);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static String getRadomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = RANDOM.nextInt(62);
            if (number < 26) {
                char ch = (char) ('a' + number);
                sb.append(ch);
            } else if(number < 52) {
                char ch = (char) ('A' + number-26);
                sb.append(ch);
            } else {
                char ch = (char) ('0' + number-52);
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String token = getToken("hello");
        getUserId(token.getBytes(StandardCharsets.UTF_8));
    }
}
