package com.vabrant.playground.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.function.Consumer;

public class CopyFileCommand implements Command<File, Object> {

    private Consumer<File> options;
    private final MacroCommand<File, Object> macroCommand;

    public CopyFileCommand(File srcFile, File destFile) throws Exception {
//        if (!srcFile.exists()) throw new FileNotFoundException("File at " + srcFile.getAbsolutePath() + " not found.");
        macroCommand = new MacroCommand<>(new ReadAsBytesCommand(srcFile), new WriteToFileCommand(destFile));
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
        File file = macroCommand.execute();
        if (options != null) options.accept(file);
        return file;
    }
}
