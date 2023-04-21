package com.vabrant.playground;

import com.playground.commands.Callback;

public class ProjectErrorCallback implements Callback {

    private final PlaygroundUtils.LogLevel logLevel;
    private Project project;

    public ProjectErrorCallback(PlaygroundUtils.LogLevel logLevel, Project project) {
        this.logLevel = logLevel;
        this.project = project;
    }

    @Override
    public void onCallback(Exception e) {
        project.errors();
        PlaygroundUtils.deleteDirectory(true, project.getRootDirectory());
        PlaygroundUtils.log(logLevel, PlaygroundUtils.LogLevel.ERROR, "Project", "(" + project.getName() + ") Failed to build.");
    }
}
