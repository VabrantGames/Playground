package com.vabrant.playground.test;

import com.vabrant.playground.Headless;
import org.junit.jupiter.api.Test;
import org.tomlj.Toml;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

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
