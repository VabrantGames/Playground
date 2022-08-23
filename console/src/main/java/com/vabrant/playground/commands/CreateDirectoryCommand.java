package com.vabrant.playground.commands;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateDirectoryCommand implements Command {

    private final Path path;

    public CreateDirectoryCommand(String str) {
         this(Paths.get(str));
    }

    public CreateDirectoryCommand(Path path) {
        if (path == null) throw new IllegalArgumentException("Path is null");
        this.path = path;
    }


    @Override
    public void execute() throws Exception {
        Files.createDirectories(path);
    }
}
