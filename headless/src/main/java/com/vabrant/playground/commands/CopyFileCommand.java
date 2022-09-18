package com.vabrant.playground.commands;

import com.vabrant.playground.Setup;

import java.io.File;
import java.util.function.Consumer;

public class CopyFileCommand implements Command<File, Object> {

    private final File srcFile;
    private final File destFile;
    private Consumer<File> options;

    public CopyFileCommand(File srcFile, File destFile) throws Exception {
        this.srcFile = srcFile;
        this.destFile = destFile;

        //TODO Add description
        if (!srcFile.exists()) throw new RuntimeException("");
    }

    /**
     * Options to be executed on the file after it has been copied.
     * @param options
     * @return
     */
    public CopyFileCommand options(Consumer<File> options) {
        this.options = options;
        return this;
    }

    @Override
    public File execute() throws Exception {
        Setup.copy(srcFile, destFile);
        if (options != null) options.accept(destFile);
        return destFile;
    }
}
