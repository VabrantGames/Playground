package com.vabrant.playground.test;

import com.vabrant.playground.ProjectData;
import com.vabrant.playground.Utils;
import org.junit.jupiter.api.Test;
import org.tomlj.Toml;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.function.Consumer;

public class TomlTest {

    private static final String SETTINGS_PATH = "src/test/java/resources/settingsMockup.toml";

    @Test
    public void readTest() throws Exception {
        TomlParseResult result = Toml.parse(new FileInputStream(new File(SETTINGS_PATH)));
        String s = (String) result.get("name");
        System.out.println(s);
    }

    @Test
    public void readTableTest() throws Exception {
        TomlParseResult result = Toml.parse(new FileInputStream(new File(SETTINGS_PATH)));
        TomlTable t = result.getTable("projects");

        for (String s : t.keySet()) {
            System.out.println(s);
        }
    }

    @Test
    public void readTemplatesTest() throws Exception {
        TomlParseResult result = Toml.parse(new FileInputStream(new File(SETTINGS_PATH)));
        TomlTable t = result.getTable("templates");

        TomlTable bt = t.getTable("templatetest");

        for (String s : t.dottedKeySet()) {
            System.out.println(s);
        }
    }

    public static Map<String, String> createMap(Consumer<Map<String, String>> c) {
        Map<String, String> map = new HashMap<>();
        c.accept(map);
        return map;
    }

    private Map<String, Object> asMap(Object o) {
        return (Map) o;
    }

    private void addToMap(Map root, String key, Object value) {
        asMap(root).put(key, value);
    }

}
