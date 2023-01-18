package com.vabrant.playground;

import com.moandjiezana.toml.Toml;

import java.io.File;
import java.util.Map;

public class Playground {

    private String name;
    private String nameLowerCase;
    private String group;
    private File playgroundDirectory;
    private File projectsDirectory;
    private File settingsFile;
    private Toml settings;

    Playground(File rootDirectory) {
        playgroundDirectory = new File(rootDirectory, "playground");
        projectsDirectory = new File(playgroundDirectory, "projects");
    }

    public void setup(PlaygroundCommandData data) {
        name = data.getName();
        nameLowerCase = name.toLowerCase();
        group = data.getGroup();
    }

    public void setup(Settings settings) {

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

    public File getPlaygroundDirectory() {
        return playgroundDirectory;
    }

    public File getProjectsDirectory() {
        return projectsDirectory;
    }

    public File getSettingsFile() {
        return settingsFile;
    }

    public Toml getSettings() {
        return settings;
    }
}
