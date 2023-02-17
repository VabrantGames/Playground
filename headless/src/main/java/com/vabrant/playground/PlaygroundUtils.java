package com.vabrant.playground;

import com.github.tommyettinger.ds.ObjectObjectMap;
import org.tomlj.TomlArray;
import org.tomlj.TomlTable;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

public class PlaygroundUtils {

//    public static final int LOGGER_QUIET = 0;
//    public static final int LOGGER_ERROR = 1;
//    public static final int LOGGER_INFO = 2;
//    public static final int LOGGER_DEBUG = 3;

    public enum LogLevel {
        QUIET(0),
        ERROR(1),
        INFO(2),
        DEBUG(3);

        final int level;

        LogLevel(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }

    public static final StringBuilder LOGGER_BUILDER = new StringBuilder(300);

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

    public static String prettyPrintSet(boolean includeQuotes, Collection<String> set) {
        StringBuilder builder = new StringBuilder();

        builder.append('[');
        int idx = 0;
        for (String s : set) {
            if (includeQuotes) {
                builder.append('"');
                builder.append(s);
                builder.append('"');
            } else {
                builder.append(s);
            }
            if (++idx < set.size()) {
                builder.append(',');
            }
        }
        builder.append(']');

        return builder.toString();
    }

    public static String writeTomlTableToString(String name, TomlTable t) {
        String table = " ";
        StringBuilder builder = new StringBuilder(200);
        Set<Map.Entry<String, Object>> set = t.dottedEntrySet();
        for (Map.Entry<String, Object> e : set) {
            String key = e.getKey();
            Object value = e.getValue();

            if (key.contains(".")) {
                if (!key.startsWith(table)) {
                    table = key.substring(0, key.indexOf('.'));
                    builder.append(System.lineSeparator());
                }
            }

            if (name != null) {
                builder.append(name);
                builder.append('.');
            }
            builder.append(e.getKey());
            builder.append(" = ");

            if (value instanceof String) {
                builder.append('"');
                builder.append((String) value);
                builder.append('"');

                builder.append(System.lineSeparator());
            } else if (value instanceof Boolean) {
                builder.append((boolean) value);
                builder.append(System.lineSeparator());
            } else if (value instanceof Number) {
                builder.append(value);

                builder.append(System.lineSeparator());
            } else if (value instanceof TomlArray) {
                builder.append(prettyPrintSet(true, (List) ((TomlArray) value).toList()));

                builder.append(System.lineSeparator());
            } else {
                builder.append("unsupported");
            }
        }

        return builder.toString();
    }

    public static Map<String, String> createMap(Map<String, String> m, Consumer<Map<String, String>> c) {
        Map<String, String> map = new ObjectObjectMap<>(m);
        c.accept(map);
        return map;
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

    public static void deleteDirectory(boolean deleteRoot, File file) {
        File[] files = file.listFiles();

        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDirectory(true, f);
                } else {
                    f.delete();
                }
            }
        }

        if (deleteRoot) file.delete();
    }

    public static boolean isDirectoryEmpty(File directory) {
        return directory.list().length == 0;
    }

    public static void log(LogLevel globalLevel, LogLevel messageLevel, String header, String message) {
        if (messageLevel.getLevel() > globalLevel.getLevel()) return;

        StringBuilder builder = new StringBuilder(100);

        if (messageLevel.equals(LogLevel.ERROR)) {
            builder.append('[');
            builder.append(messageLevel.name());
            builder.append("] ");
            builder.append(header);
            builder.append(" : ");
            builder.append(message);
            System.err.println(builder.toString());
        } else {
            if (header != null) {
                builder.append('[');
                builder.append(globalLevel.name());
                builder.append("] ");
                builder.append(header);
                builder.append(" : ");

            }
            builder.append(message);
            System.out.println(builder.toString());
        }
    }
}
