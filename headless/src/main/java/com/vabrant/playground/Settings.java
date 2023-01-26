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
import java.util.Set;

public class Settings {

    private Map<String, Map<String, Object>> projectsMap;
    private Map<String, Map<String, Object>> templateMap;
    private TomlParseResult result;
    private TomlTable projectsTable;
    private TomlTable templatesTable;

    public boolean hasProjectName(String name) {
        TomlTable t = getProjectsTable();
        return t == null || t.contains(name);
    }

    public void load(File settingsFile) throws IOException {
       result = Toml.parse(new FileInputStream(settingsFile)) ;
    }

    public TomlTable getProjectsTable() {
        if (result != null && projectsMap == null) {
            projectsTable = result.getTable("projects");
        }
        return projectsTable;
    }

    public Map<String, Object> getProjectsMap() {
        TomlTable t = getProjectsTable();
        return t == null ? null : t.toMap();
    }

    public TomlTable getTemplatesTable() {
       return templatesTable;
    }

    public Map<String, Object> getTemplatesMap() {
        TomlTable t = getTemplatesTable();
        return t == null ? null : t.toMap();
    }

    public Set<Map.Entry<String, Object>> getProjectsSet() {
        TomlTable t = getProjectsTable();
        return t == null ? null : t.dottedEntrySet();
    }

    public List<String> getLaunchers(String projectName) {
        return (List) getProjectsTable().getTable(projectName).getArray("launchers").toList();
    }

    public Map<String, Map<String, ArrayList<String>>> getTemplateMap(String templateName) {
        return (Map) getTemplatesMap().get(templateName);
//        return (Map) templateMap.get(templateName);
    }
}
