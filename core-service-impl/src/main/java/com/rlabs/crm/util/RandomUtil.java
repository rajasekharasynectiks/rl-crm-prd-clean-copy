package com.rlabs.crm.util;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomUtil {

    public static String getRandomStringWithSpecialChars(int length) {
        String characters = "!@$ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String pwd = RandomStringUtils.random( length, characters );
        return pwd;
    }

    public static String getRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String pwd = RandomStringUtils.random( length, characters );
        return pwd;
    }

    public static String getRandomNumber(int length) {
        String characters = "0123456789";
        String pwd = RandomStringUtils.random( length, characters );
        return pwd;
    }
}
