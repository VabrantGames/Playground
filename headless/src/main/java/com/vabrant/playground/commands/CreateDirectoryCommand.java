package com.vabrant.playground.commands;

import java.io.File;
import java.util.function.Consumer;

public class CreateDirectoryCommand implements Command<File, Object> {

    private final File directory;


    public CreateDirectoryCommand(File directory) {
        this.directory = directory;
    }

    public CreateDirectoryCommand options(Consumer<File> r) {
        r.accept(directory);
        return this;
    }

    @Override
    public File execute(Object data) throws Exception {
        if (!directory.mkdirs()) throw new RuntimeException("Error creating folder " + directory.getName());
        return directory;
    }
}
