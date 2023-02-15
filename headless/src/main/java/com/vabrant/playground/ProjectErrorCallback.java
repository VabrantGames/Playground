package com.vabrant.playground;

public class ProjectErrorCallback implements Callback {

    private final int logLevel;
    private Project project;

    public ProjectErrorCallback(int logLevel, Project project) {
        this.logLevel = logLevel;
        this.project = project;
    }

    @Override
    public void onCallback() {
        project.errors();
        PlaygroundUtils.deleteDirectory(true, project.getRootDirectory());
        PlaygroundUtils.log(logLevel, PlaygroundUtils.LOGGER_ERROR, "Project", "(" + project.getName() + ") Failed to build.");
    }
}
