package com.vabrant.playground;

import com.github.tommyettinger.ds.ObjectList;
import org.tomlj.Toml;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Settings {

    private TomlParseResult result;
    private TomlTable projectsTable;
    private TomlTable templatesTable;
    private TomlTable launchersTable;
    private Set<String> templateNames;
    private Set<String> launcherNames;
    private List<String> projectNames;

    public boolean hasProjectName(String name) {
        TomlTable t = getProjectsTable();
        return t != null && t.contains(name);
    }

    public void load(File settingsFile) throws IOException {
        result = Toml.parse(new FileInputStream(settingsFile));
        setNames();
    }

    public void loadDefaults() throws IOException {
        result = Toml.parse(Settings.class.getResourceAsStream("/setup/playground/defaults.toml"));
        setNames();
    }

    private void setNames() {
        templateNames = getTemplatesTable().keySet();
        launcherNames = getLaunchersTable().keySet();

        TomlArray arr = result.getArray("projects");
        if (arr == null) {
           projectNames = new ObjectList<>();
        } else {
           projectNames = (List) arr.toList();
        }
//        projectNames = (List) result.getArray("projects").toList();
    }

    public List<String> getProjectNames() {
        return projectNames;
    }

    public Set<String> getLauncherNames() {
        return launcherNames;
    }

    public Set<String> getTemplateNames() {
        return templateNames;
    }

    public TomlTable getMainTable() {
        return result;
    }

    public TomlTable getProjectsTable() {
        if (result != null && projectsTable == null) {
            projectsTable = result.getTable("projects");
        }
        return projectsTable;
    }

    public Map<String, Object> getProjectsMap() {
        TomlTable t = getProjectsTable();
        return t == null ? null : t.toMap();
    }

    public TomlTable getTemplatesTable() {
        if (result != null && templatesTable == null) {
            templatesTable = result.getTable("templates");
        }
        return templatesTable;
    }

    public TomlTable getLaunchersTable() {
        if (launchersTable == null) {
            launchersTable = result.getTable("launchers");
        }
        return launchersTable;
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
