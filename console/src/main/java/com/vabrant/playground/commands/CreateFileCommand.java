package com.vabrant.playground.commands;

import java.nio.file.Files;
import java.nio.file.Path;

public class CreateFileCommand implements Command {

    private final Path path;

    public CreateFileCommand(Path path) {
        if (path == null) throw new IllegalArgumentException("Path is null");
        this.path = path;
    }

    @Override
    public void execute() throws Exception {
        Files.createFile(path);
    }
}
