package com.vabrant.playground.commands;

import com.vabrant.playground.Project;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ChangeGradleSettingsCommand implements Command<String, Object> {

    private final String includeString = "include ':";
    private final String includeBeginString = "//Include-Begin";
    private final String projectBeginString = "//Project-Begin";

    private final List<Project> projects;
    private String str;

    public ChangeGradleSettingsCommand(List<Project> projects) {
        this.projects = projects;
    }

    @Override
    public void setData(Object data) {
        if (!(data instanceof byte[])) throw new RuntimeException("Invalid Gradle settings data");
        str = new String((byte[]) data, StandardCharsets.UTF_8);
    }

    private void loadIncludesSection(Map<String, ArrayList<String>> map, String str) {
        int beginIdx = str.indexOf(includeBeginString);
        int endIdx = str.indexOf("//Include-End");

        String[] split = str.substring(beginIdx, endIdx).split(includeString);
        for (String s : split) {
            s = s.strip();
            if (s.startsWith("//")) continue;

            s = s.replaceAll("[':]", "");

            String name = null;
            String launcher = null;

            int lastIdx = s.lastIndexOf('-');

            if (lastIdx == -1) {
                name = s;
            } else {
                name = s.substring(0, lastIdx);
                launcher = s.substring(lastIdx + 1);
            }

            if (!map.containsKey(name)) map.put(name, new ArrayList<>());

            if (launcher != null) {
                map.get(name).add(launcher);
            }
        }
    }

    private void loadProjectSection(Map<String, ArrayList<String>> map, String str) {
        int beginIdx = str.indexOf(projectBeginString);
        int endIdx = str.indexOf("//Project-End");

        String[] split = str.substring(beginIdx, endIdx).split("project\\(':");
        for (String s : split) {
            s = s.strip();
            if (s.startsWith("//")) continue;
            s = s.substring(0, s.indexOf('\''));

            String name = null;
            String launcher = null;

            int lastIdx = s.lastIndexOf('-');

            if (lastIdx == -1) {
                name = s;
            } else {
                name = s.substring(0, lastIdx);
                launcher = s.substring(lastIdx + 1);
            }

            if (!map.containsKey(name)) map.put(name, new ArrayList<>());

            if (launcher != null) {
                map.get(name).add(launcher);
            }
        }
    }

    private void addProjectsToSection(Map<String, ArrayList<String>> map) {
        for (Project p : projects) {
            if (!map.containsKey(p.getNameLowerCase())) {
                map.put(p.getNameLowerCase(), new ArrayList<>());
            }
        }
    }

    private void buildIncludeString(StringBuilder builder, Map<String, ArrayList<String>> map) {
        AtomicInteger idx = new AtomicInteger();
        map.forEach((k, v) -> {
            builder.append(includeString);
            builder.append(k);
            builder.append('\'');

            if (!v.isEmpty()) {
                for (String s : v) {
                    builder.append(System.lineSeparator());
                    builder.append(includeString);
                    builder.append(k);
                    builder.append('-');
                    builder.append(s);
                    builder.append('\'');
                }
            }

            if (idx.getAndIncrement() < map.size() - 1) {
            }
            builder.append(System.lineSeparator());
        });
    }

    private void buildProjectsString(StringBuilder builder, Map<String, ArrayList<String>> map) {
        AtomicInteger idx = new AtomicInteger();
        map.forEach((k, v) -> {
            builder.append("project(':");
            builder.append(k);
            builder.append("').projectDir = new File('projects/");
            builder.append(k);
            builder.append("')");

            if (!v.isEmpty()) {
                for (String s : v) {
                    builder.append(System.lineSeparator());
                    builder.append("project(':");
                    builder.append(k);
                    builder.append('-');
                    builder.append(s);
                    builder.append("').projectDir = new File('projects/");
                    builder.append(k);
                    builder.append("/launchers/");
                    builder.append(s);
                    builder.append("')");
                }
            }

            if (idx.getAndIncrement() < map.size() - 1) {
                builder.append(System.lineSeparator());
            }
        });

    }

    @Override
    public String execute() throws Exception {
        String preGeneratorStuff = str.substring(0, str.indexOf(includeBeginString));
        Map<String, ArrayList<String>> includeMap = new HashMap<>();
        Map<String, ArrayList<String>> projectMap = new HashMap<>();
        StringBuilder builder = new StringBuilder(2000);

        builder.append(preGeneratorStuff);

        loadIncludesSection(includeMap, str);
        addProjectsToSection(includeMap);
        loadProjectSection(projectMap, str);
        addProjectsToSection(projectMap);

        builder.append(includeBeginString);
        builder.append(System.lineSeparator());
        buildIncludeString(builder, includeMap);
        builder.append("//Include-End");
        builder.append(System.lineSeparator());
        builder.append(System.lineSeparator());
        builder.append(projectBeginString);
        builder.append(System.lineSeparator());
        buildProjectsString(builder, projectMap);
        builder.append(System.lineSeparator());

        builder.append(str.substring(str.indexOf("//Project-End")));
        return builder.toString();
    }
}
