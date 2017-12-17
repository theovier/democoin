package com.theovier.democoin.common;

public class Utils {

    public static String escapeText(final String text) {
        String escaped = text;
        if (text.length() > 255) {
            escaped = text.substring(0, 255);
        }
        escaped = escaped.replace(">", ")");
        escaped = escaped.replace("<", "(");
        return escaped;
    }

    public static String fillTo64ByteWithLeadingZeros(final String hextext) {
        int difference = 64 - hextext.length();
        if (difference <= 0) {
            return hextext;
        }
        StringBuilder builder = new StringBuilder(hextext).reverse();
        for (int i=0; i < difference; i++) {
            builder.append("0");
        }
        return builder.reverse().toString();
    }

}
