package com.vabrant.playground.commands;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

public class CreateDirectoryCommand implements Command<Path, Object> {

    private final File directory;

    public CreateDirectoryCommand(File directory) throws Exception {
        if (directory == null) throw new IllegalArgumentException("Path is null");
        this.directory = directory;
    }

    public CreateDirectoryCommand options(Consumer<File> r) {
        r.accept(directory);
        return this;
    }

    @Override
    public Path execute() throws Exception {
        directory.mkdirs();
        return null;
    }
}
