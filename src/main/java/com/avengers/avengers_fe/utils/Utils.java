package com.avengers.avengers_fe.utils;

public class Utils {
    public static String generateEmail(String hero) {
        return hero.indexOf('@') >= 0 ? hero : hero.toLowerCase() + "@avengers.com";
    }
}
