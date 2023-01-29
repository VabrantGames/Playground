package com.vabrant.playground.commands;

import java.io.File;
import java.util.function.Consumer;

public class CreateDirectoryCommand implements Command<File, Object> {

    private String dirs;
    private File originalDirectory;
    private final File directory;

    public CreateDirectoryCommand(File directory, String dirs) {
        this(new File(directory, dirs));
        originalDirectory = directory;
        this.dirs = dirs;
    }

    public CreateDirectoryCommand(File directory) {
        this.directory = directory;
    }

    @Override
    public void revert() {
        if (directory != null && directory.isDirectory()) {
            System.out.println("Delete Directory: " + directory.getPath());

            if (dirs != null) {
                deleteDirectories(originalDirectory);
            } else {
                directory.delete();
            }
        }
    }

    private void deleteDirectories(File file) {
       File[] dirs = file.listFiles();

       if (dirs.length > 0) {
          deleteDirectories(dirs[0]) ;
       }

       file.delete();
    }

    public CreateDirectoryCommand options(Consumer<File> r) {
        r.accept(directory);
        return this;
    }

    @Override
    public File execute(Object data) throws Exception {
        directory.mkdirs();
        return directory;
    }
}
