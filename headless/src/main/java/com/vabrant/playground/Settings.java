package com.vabrant.playground;

import org.tomlj.Toml;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Settings {

    //    private final Map<String, Object> settingsMap;
    private Map<String, Map<String, Object>> projectsMap;
    private Map<String, Map<String, Object>> templateMap;
    private TomlParseResult result;
    private TomlTable projectsTable;

    public Settings(File settingsFile) throws IOException {
        result = Toml.parse(new FileInputStream(settingsFile));
//        settingsMap = result.toMap();
    }

    public boolean hasProjectName(String name) {
        return getProjectsTable().contains(name);
//        return projectsMap.containsKey(name);
    }

    public TomlTable getProjectsTable() {
        if (projectsMap == null) {
            projectsTable = result.getTable("projects");
        }
        return projectsTable;
    }

    public List<String> getLaunchers(String projectName) {
        return (List) getProjectsTable().getTable(projectName).getArray("launchers").toList();
    }

    public Map<String, Map<String, Object>> getTemplatesMap() {
        if (templateMap == null) {
//            templateMap = (Map) settingsMap.get("templates");
        }
        return (Map) templateMap;
    }

    public Map<String, Map<String, ArrayList<String>>> getTemplateMap(String templateName) {
        return (Map) getTemplatesMap().get(templateName);
//        return (Map) templateMap.get(templateName);
    }
}
