package com.vabrant.playground.commands;

import com.vabrant.playground.Project;
import com.vabrant.playground.Settings;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ChangeSettingsFileCommand implements Command<String, byte[]> {

    private Settings settings;
    private List<Project> projects;
    private String str;

    public ChangeSettingsFileCommand(Settings settings, List<Project> projects) {
        this.settings = settings;
        this.projects = projects;
    }

    @Override
    public void setData(byte[] data) {
        str = new String(data, StandardCharsets.UTF_8);
    }

    @Override
    public String execute() throws Exception {
        int beginIdx = str.indexOf("Projects Begin");
        int endIdx = str.indexOf("Projects End");

        String[] split = str.substring(beginIdx, endIdx).split("projects");
        for (String s : split) {
            System.out.println(s);
        }
        return null;
    }
}
