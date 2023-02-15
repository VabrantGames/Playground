package com.vabrant.playground;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class Project {

    private boolean errors;
    private boolean isNewProject;
    private ArrayList<String> launchers;
    private String name;
    private String nameLowerCase;
    private String passedInTemplateName;
    private File rootDirectory;
    private File sourceDirectory;
    private File launchersDirectory;
    private Map<String, String> replaceMap;

    public Project(String name, File projectsDirectory) {
        this.name = name;
        nameLowerCase = name.toLowerCase();
        rootDirectory = new File(projectsDirectory, nameLowerCase);
        launchers = new ArrayList<>();
        launchersDirectory = new File(rootDirectory, "launchers");
    }

    public void setReplaceMap(Map<String, String> replaceMap) {
        this.replaceMap = replaceMap;
    }

    public Map<String, String> getReplaceMap() {
        return replaceMap;
    }

    public void createSourceDirectory(Playground playground) {
        sourceDirectory = new File(rootDirectory, "src/main/java/" + replaceMap.get(Headless.GROUP_TAG).replace('.', '/'));
    }

    public void setPassedInTemplate(String name) {
        passedInTemplateName = name;
    }

    public String getPassedInTemplate() {
        return passedInTemplateName;
    }

    public void addLauncher(String launcher) {
        launchers.add(launcher);
    }

    public ArrayList<String> getLaunchers() {
        return launchers;
    }

    public void errors() {
        this.errors = true;
    }

    public boolean hasErrors() {
        return errors;
    }

    public boolean isNewProject() {
        return isNewProject;
    }

    public void newProject() {
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
