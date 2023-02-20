package com.vabrant.playground;

import org.tomlj.TomlTable;

import java.io.File;

public class Playground {

    private boolean isStandalone;

    private boolean isNewPlayground;
    private String name;
    private String nameLowerCase;
    private String group;
    private File playgroundDirectory;
    private File projectsDirectory;
    private File rootDirectory;
    private File tomlSettingsFile;
    private File settingsGradleFile;

    Playground(File rootDirectory, File playgroundDirectory) {
        this.rootDirectory = rootDirectory;
        this.playgroundDirectory = playgroundDirectory; projectsDirectory = new File(playgroundDirectory, "projects");
    }

    public void setup(String name) {
        this.name = name;
        nameLowerCase = name.toLowerCase();
        group = "com.playground." + nameLowerCase;
    }

    public void setup(Settings settings) {
        TomlTable table = settings.getMainTable();
        name = table.getString("name");

        if (name == null) throw new RuntimeException("Error setting up Playground from Settings.toml. Name is null");

        nameLowerCase = name.toLowerCase();
        group = "com.playground." + nameLowerCase;
    }

    public void setStandalone() {
        isStandalone = true;
    }

    public boolean isStandalone() {
        return isStandalone;
    }

    public void newPlayground() {
        isNewPlayground = true;
    }

    public boolean isNewPlayground() {
        return isNewPlayground;
    }

    public String getName() {
        return name;
    }

    public String getNameLowerCase() {
        return nameLowerCase;
    }

    public String getGroup() {
        return group;
    }

    public File getRootDirectory() {
        return rootDirectory;
    }

    public File getPlaygroundDirectory() {
        return playgroundDirectory;
    }

    public File getProjectsDirectory() {
        return projectsDirectory;
    }

}
