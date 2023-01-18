package com.vabrant.playground;

import java.util.*;

public class ProjectData {

    private boolean isLibgdxCompatible;
    private String projectName;
    private final List<String> launchers;

    public ProjectData() {
        launchers = new ArrayList<>();
    }

    public ProjectData setLibgdxCompatible(boolean isLibgdxCompatible) {
        this.isLibgdxCompatible = isLibgdxCompatible;
        return this;
    }

    public ProjectData setProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public ProjectData launchers(String... l) {
        launchers.addAll(Arrays.asList(l));
        return this;
    }

    public boolean setLibgdxCompatible() {
        return isLibgdxCompatible;
    }

    public String getProjectName() {
        return projectName;
    }

    public List<String> getLaunchers() {
        return launchers;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<>();
        m.put("isLibgdxCompatible", isLibgdxCompatible);
        m.put("projectName", projectName);
        m.put("launchers", launchers);
        return m;
    }
}
