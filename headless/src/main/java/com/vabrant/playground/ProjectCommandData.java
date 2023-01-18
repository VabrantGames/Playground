package com.vabrant.playground;

import picocli.CommandLine;

@CommandLine.Command(name = "Project")
public class ProjectCommandData {

    private boolean newProject;

    @CommandLine.Option(names = {"-p", "--project"})
    private String name;

    @CommandLine.Option(names = {"-d", "--dependencies"}, arity = "0..*")
    String[] dependencies;

    @CommandLine.Option(names = {"-l", "--launcheers"}, arity = "0..*")
    String[] launchers;

    String template;

    public void setNewProject(boolean newProject) {
        this.newProject = newProject;
    }

    public boolean isNewProject() {
        return newProject;
    }

    public String getName() {
        return name;
    }

    public String[] getLaunchers() {
        return launchers;
    }

    public void setTemplateString(String template) {
        this.template = template;
    }

    public String getTemplateString() {
        return template;
    }

}
