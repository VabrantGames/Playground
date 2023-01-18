package com.vabrant.playground;

import java.util.*;

public class AppInfo {

    private String appName;
    private String packageName;

    private Map<String, Object> settings;
    private Map<String, List<String>> playgrounds;

    public AppInfo() {
        playgrounds = new HashMap<>();
    }

    public Set<String> getPlaygroundNames() {
        return playgrounds.keySet();
    }

    public List<String> getProjectNames(String playgroundName) {
        return playgrounds.get(playgroundName);
    }

    public void load(Map<String, Object> settings) {
        this.settings = settings;

        Map<String, Object> playgrounds = Utils.asMap(settings.get("playgrounds"));

        for (Map.Entry<String, Object> o : playgrounds.entrySet()) {
            List<String> projects = new ArrayList<>();

            Map<String, Object> playground = Utils.asMap(playgrounds.get(o.getKey()));
            projects.addAll(playground.keySet());

            this.playgrounds.put(o.getKey(), projects);
        }

    }
}
