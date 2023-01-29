package com.vabrant.playground.commands;

import com.github.tommyettinger.ds.ObjectSet;
import com.vabrant.playground.Project;
import com.vabrant.playground.Settings;
import org.tomlj.TomlArray;
import org.tomlj.TomlTable;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ChangeSettingsFileCommand implements Command<String, byte[]> {

    private Settings settings;
    private List<Project> projects;
    private String str;

    public ChangeSettingsFileCommand(Settings settings, List<Project> projects) {
        this.settings = settings;
        this.projects = projects;
    }

//    @Override
//    public void setData(byte[] data) {
////        str = new String(data, StandardCharsets.UTF_8);
//    }

    private void buildTomlString(StringBuilder builder, Map<String, ObjectSet<String>> map) {
        map.forEach((k, v) -> {
            builder.append(k);
            builder.append(" = [");

            if (!v.isEmpty()) {
                AtomicInteger idx = new AtomicInteger();
                v.forEach(s -> {
                    builder.append('\"');
                    builder.append(s);
                    builder.append('\"');

                    if (idx.getAndIncrement() < (v.size() - 1)) {
                        builder.append(", ");
                    }
                });
            }

            builder.append(']');
            builder.append(System.lineSeparator());
        });
    }

    @Override
    public String execute(byte[] data) throws Exception {
        str = new String(data, StandardCharsets.UTF_8);

        Map<String, ObjectSet<String>> allProjects = new HashMap<>();

        //Add existing settings
        if (settings.getProjectsMap() != null) {
            Set<Map.Entry<String, ?>> set = (Set) settings.getProjectsSet();

            for (Map.Entry<String, ?> e : set) {
                List<String> l = (List) ((TomlArray) e.getValue()).toList();
                allProjects.put("projects." + e.getKey(), new ObjectSet<>(l));
            }
        }

        //Add new settings
        for (Project p : projects) {
            String key = "projects." + p.getNameLowerCase() + ".launchers";

            if (!allProjects.containsKey(key)) {
                allProjects.put(key, new ObjectSet<>());
            }

            if (!p.getNewLaunchers().isEmpty()) {
                ObjectSet<String> newLaunchers = allProjects.get(key);

                for (String s : p.getNewLaunchers()) {
                   newLaunchers.add(s);
                }
            }

        }

        StringBuilder builder = new StringBuilder(200);
        builder.append(str.substring(0, str.indexOf("#Projects Begin")));
        builder.append("#Projects Begin");
        builder.append(System.lineSeparator());
        buildTomlString(builder, allProjects);
        builder.append(str.substring(str.indexOf("#Projects End")));

        return builder.toString();
    }
}
