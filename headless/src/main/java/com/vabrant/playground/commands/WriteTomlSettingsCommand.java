package com.vabrant.playground.commands;

import com.github.tommyettinger.ds.ObjectList;
import com.vabrant.playground.Playground;
import com.vabrant.playground.PlaygroundUtils;
import com.vabrant.playground.Project;
import com.vabrant.playground.Settings;

import java.util.List;

public class WriteTomlSettingsCommand implements Command<String, byte[]> {

    private String playgroundName;
    private Settings settings;
    private Playground playground;
    private List<Project> projects;

    public WriteTomlSettingsCommand(Playground playground, List<Project> projects, Settings settings) {
        this.playground = playground;
        this.projects = projects;
        this.settings = settings;
    }

    private List<String> createProjectsArray() {
        List<String> list = new ObjectList<>(settings.getProjectNames());

        if (projects == null || projects.isEmpty()) return list;

        if (list.isEmpty()) {
            for (Project p : projects) {
                list.add(p.getName());
            }
        } else {
            outer:
            for (Project p : projects) {
                for (String s : list) {
                   if (s.equalsIgnoreCase(p.getName())) {
                       continue outer;
                   }
                }
                list.add(p.getName());
            }
        }
        return list;
    }

    @Override
    public String execute(byte[] data) throws Exception {
        StringBuilder builder = new StringBuilder(200);
        builder.append("# This is a generated TOML document");
        builder.append(System.lineSeparator());
        builder.append("# This file should not be edited manually");
        builder.append(System.lineSeparator());
        builder.append(System.lineSeparator());

        builder.append("name = \"");
        builder.append(playground.getName());
        builder.append('"');

        builder.append(System.lineSeparator());
        builder.append("projects = ");
        builder.append(PlaygroundUtils.prettyPrintSet(true, createProjectsArray()));

        builder.append(System.lineSeparator());
        builder.append(PlaygroundUtils.writeTomlTableToString("templates", settings.getTemplatesTable()));
        builder.append(PlaygroundUtils.writeTomlTableToString("launchers", settings.getLaunchersTable()));
        return builder.toString();
    }
}
