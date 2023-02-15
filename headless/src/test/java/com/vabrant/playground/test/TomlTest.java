package com.vabrant.playground.test;

import com.vabrant.playground.PlaygroundUtils;
import org.junit.jupiter.api.Test;
import org.tomlj.Toml;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class TomlTest {

    private static final String SETTINGS_PATH = "src/test/java/resources/settingsMockup.toml";
    private final String settingsPath = "/settingsMockup.toml";

    @Test
    public void resourceTest() {
        URL u = TomlTest.class.getResource("/Hello.txt");
        System.out.println(u.getPath());
    }

    private TomlParseResult readSettingsFile() throws IOException {
        return Toml.parse(TomlTest.class.getResourceAsStream("/settingsMockup.toml"));
    }

    @Test
    void bob() throws Exception {
        TomlParseResult result = readSettingsFile();
        TomlTable t = result.getTable("hello");

//        writeTable("", result);
        String s = PlaygroundUtils.writeTomlTableToString(null, result);
        System.out.println(s);

        t.dottedEntrySet().forEach((e) -> {
            String key = e.getKey();
            Object value = e.getValue();


        });

    }

    private String writeTable(String str, TomlTable t) {
        Set<Map.Entry<String, Object>> set = t.dottedEntrySet();
            String table = " ";
        for (Map.Entry<String, Object> e : set) {
            String key = e.getKey();
            Object value = e.getValue();

            if (key.contains(".")) {
                if (!key.startsWith(table)) {
                   table = key.substring(0, key.indexOf('.'));
                   System.out.println();
                }
            }


            System.out.print(e.getKey() + " = ");

            if (value instanceof String) {
                System.out.println('"' + (String) value + '"');
            } else if (value instanceof Boolean) {
                System.out.println('"' + Boolean.toString((Boolean) value) + '"');
            } else if (value instanceof Number) {
                System.out.println(value);
            } else {
                System.out.println("Unsupported");
            }

        }

        t.dottedEntrySet().forEach((e) -> {
            String k = e.getKey();
            Object v = e.getValue();
        });
        return null;
    }

    @Test
    void ReadTest() throws Exception {
        TomlParseResult result = readSettingsFile();
        assertEquals("Settings", result.get("name"));
    }

    @Test
    public void ReadTableTest() throws Exception {
        TomlParseResult result = readSettingsFile();
        TomlTable t = result.getTable("projects");

        assertTrue(t.isTable("bird"));
        assertTrue(t.isTable("helloworld"));
    }

    @Test
    public void ReadListTest() throws Exception {
        TomlParseResult result = readSettingsFile();
        TomlTable t = result.getTable("templates").getTable("templatetest").getTable("root");

        Map<String, Object> tm = (Map) t.toMap();
        assertTrue(tm.containsKey(""));
        assertTrue(tm.containsKey("bob"));

        List list = ((TomlArray) tm.get("")).toList();
        assertTrue(list.contains("buildGradle"));
        assertTrue(list.contains("bob"));
    }

}
