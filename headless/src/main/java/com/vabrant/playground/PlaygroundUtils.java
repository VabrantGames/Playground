package com.vabrant.playground;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PlaygroundUtils {

    private static final String[] RESTRICTED_KEYWORDS = {
            "assets",
            "launchers"
    };

    public static boolean isKeywordRestricted(String keyword) {
        for (String s : RESTRICTED_KEYWORDS) {
            if (s.equals(keyword)) return true;
        }
        return false;
    }

    public static Map<String, String> createMap(Consumer<Map<String, String>> c) {
        Map<String, String> map = new HashMap<>();
        c.accept(map);
        return map;
    }

    public static Map<String, Object> asMap(Object o) {
        return (Map) o;
    }

    public static String[] splitByChar(char[] chars, char splitChar) {
        ArrayList<String> list = new ArrayList<>(4);

        int startIdx = 0;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if (c == splitChar) {
                list.add(new String(Arrays.copyOfRange(chars, startIdx, i)));
                startIdx = i + 1;
            }
        }

        list.add(new String(Arrays.copyOfRange(chars, startIdx, chars.length)));

        return list.toArray(new String[list.size()]);
    }
}
