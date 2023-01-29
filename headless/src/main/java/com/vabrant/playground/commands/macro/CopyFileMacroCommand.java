package com.vabrant.playground.commands.macro;

import com.vabrant.playground.commands.Command;
import com.vabrant.playground.commands.ReadAsBytesCommand;
import com.vabrant.playground.commands.WriteToFileCommand;

import java.io.File;
import java.util.function.Consumer;

public class CopyFileMacroCommand implements Command<File, Object> {

    private Consumer<File> options;
    private final MacroCommand<File, Object> macroCommand;

    public CopyFileMacroCommand(File srcFile, File destFile) {
        macroCommand = new MacroCommand<>(new ReadAsBytesCommand(srcFile), new WriteToFileCommand(destFile));
    }

    /**
     * Options to be executed on the file after it has been copied.
     * @param options
     * @return
     */
    public CopyFileMacroCommand options(Consumer<File> options) {
        this.options = options;
        return this;
    }

    @Override
    public File execute(Object data) throws Exception {
        File file = macroCommand.execute(null);
        if (options != null) options.accept(file);
        return file;
    }
}
