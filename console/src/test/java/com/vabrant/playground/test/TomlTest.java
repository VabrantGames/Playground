package com.vabrant.playground.test;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TomlTest {

    @Test
    public void readTest() {
        Toml toml = new Toml().read(new File("src/test/java/resources/settingsMockup.toml"));
        System.out.println(new TomlWriter().write(toml));
    }

    @Test
    public void writeTest() {
        Toml t = new Toml().read(new File("src/test/java/resources/settingsMockup.toml"));
        TomlWriter tw = new TomlWriter();

        //User creates a new playground named writer
        String playgroundCategory = "unsorted";
        String playgroundName = "writer";

        Map<String, Object> map = t.toMap();
        Map m = addPlaygroundToSettings(map, playgroundCategory, playgroundName);
        addToMap(m, "path", "src/writer");
        addToMap(m, "type", "java");

        System.out.println(tw.write(map));
    }

    private Map<String, Object> asMap(Object o) {
        return (Map) o;
    }

    private Map addPlaygroundToSettings(Map map, String category, String playgroundName) {
        Map<String, Object> m = asMap(map.get(category));

        if (m.containsKey(playgroundName)) throw new RuntimeException("Hello Error");

        m.put(playgroundName, m = new HashMap<String, Object>());

        return m;
    }

    private void addToMap(Map root, String key, Object value) {
        asMap(root).put(key, value);
    }

}
