package com.vabrant.playground;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Project {

    private boolean isNewProject;
    private ArrayList<String> launchers;
    private ArrayList<String> newLaunchers;
    private String name;
    private String nameLowerCase;
    private File rootDirectory;
    private File sourceDirectory;
    private File launchersDirectory;

    public Project(String name, File projectsDirectory) {
        this.name = name;
        nameLowerCase = name.toLowerCase();
        rootDirectory = new File(projectsDirectory, nameLowerCase);
        newLaunchers = new ArrayList<>();
    }

    public void createSourceAndLaunchersDirectory(Playground playground) {
        sourceDirectory = new File(rootDirectory, "src/main/java/" + playground.getGroup() + '/' + nameLowerCase);
        launchersDirectory = new File(rootDirectory, "launchers");
    }

    public ArrayList<String> getNewLaunchers() {
        return newLaunchers;
    }

    public boolean isNewProject() {
        return isNewProject;
    }

    public void setNewProject() {
        isNewProject = true;
    }

    public String getName() {
        return name;
    }

    public String getNameLowerCase() {
        return nameLowerCase;
    }

    public File getRootDirectory() {
        return rootDirectory;
    }

    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public File getLaunchersDirectory() {
        return launchersDirectory;
    }
}
