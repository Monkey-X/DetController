package com.etek.controller.utils;

import java.util.regex.Pattern;

public class JsonUtil {
    //   private static String regex = "/^<([a-z]+)([^<]+)*(?:>(.*)<\\/\\1>|\\s+\\/>)$/";
//(^<[^>]+>.*<[^>]+>$|^<[^/>]+(\/>|>)$)
    private static String regex = ".*\\<[^>]+>.*";
    public static boolean isHtml(String input){


        Pattern htmlPattern = Pattern.compile(regex, Pattern.DOTALL);
        boolean isHTML = htmlPattern.matcher(input).matches();
        return isHTML;
    }
}
