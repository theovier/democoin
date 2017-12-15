package com.theovier.democoin.common;

public class Utils {


    public static String escapeText(final String text) {
        String escaped = text.substring(0, 255);
        escaped.replace(">", ")");
        escaped.replace("<", "(");
        return text;
    }

}
