package com.vabrant.playground.commands;

import com.vabrant.playground.ProjectCommandData;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

public class WriteToSettingsFileCommand implements Command {

    static final String INCLUDE_TEMPLATE_STRING = "//${include}";
    static final String PROJECT_TEMPLATE_STRING = "//${project}";

    private File settingsFile;
    private ProjectCommandData[] projects;

    public WriteToSettingsFileCommand(File settingsFile, ProjectCommandData[] projects) throws Exception {
        this.settingsFile = settingsFile;
        this.projects = projects;
    }

    private String buildIncludes() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < projects.length; i++) {
            ProjectCommandData p = projects[i];
            builder.append("include ':");
            builder.append(p.getName());
            builder.append('\'');
            builder.append(System.lineSeparator());

            if (i == projects.length - 1) {
                builder.append(INCLUDE_TEMPLATE_STRING);
            }
        }
        return builder.toString();
    }

    private String buildProjects() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < projects.length; i++) {
            ProjectCommandData p = projects[i];

            if (p.isNewProject()) {
                buildProjectString(builder, p.getName(), p.getName());
                builder.append(System.lineSeparator());
            }

//            String[] launchers = p.getLaunchers();
//            if (launchers != null) {
//                final String str = p.getName() + "/launchers/";
//                for (String s : launchers) {
//                    buildProjectString(builder, p.getName() + s, str + s);
//                    builder.append(System.lineSeparator());
//                }
//            }

            if (i == projects.length - 1) {
                builder.append(PROJECT_TEMPLATE_STRING);
            }
        }
        return builder.toString();
    }

    private void buildProjectString(StringBuilder builder, String name, String str) {
        builder.append("project(':");
        builder.append(name);
        builder.append("').projectDir = new File('projects/");
        builder.append(str);
        builder.append("')");
    }

    @Override
    public Object execute() throws Exception {
        if (!settingsFile.exists()) throw new FileNotFoundException(settingsFile.getAbsolutePath());

        ReadAsBytesCommand rabc = new ReadAsBytesCommand(settingsFile);

        String str = new String(rabc.execute(), StandardCharsets.UTF_8);
        String includeString = buildIncludes();
        String projectString = buildProjects();

        str = str.replace(INCLUDE_TEMPLATE_STRING, includeString);
        str = str.replace(PROJECT_TEMPLATE_STRING, projectString);

        System.out.println(str);

        WriteToFileCommand wtfc = new WriteToFileCommand(settingsFile);
        wtfc.setData(str);
        wtfc.execute();

        return null;
    }
}
