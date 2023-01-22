package com.vabrant.playground;

import picocli.CommandLine;

@CommandLine.Command(name = "Playground")
public class PlaygroundCommandData {

    @CommandLine.Option(
            names = "-i",
            description = "Initialize Playground")
    private boolean initialize;

    @CommandLine.Option(names = "-n")
    private String name;

    @CommandLine.Option(
            names = "-g",
            defaultValue = "com.playground")
    private String group;

    public boolean initialize() {
        return initialize;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

}
