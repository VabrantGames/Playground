package com.vabrant.playground.commands;

import com.playground.commands.Command;
import com.playground.commands.WriteToFileCommand;
import com.playground.commands.MacroCommand;

import java.io.File;

@Deprecated
public class CreateGradlePropertiesCommand implements Command<File, Object> {

    private final MacroCommand<File, Object> macroCommand;
    private final WriteToStringCommand writeToStringCommand;

    public CreateGradlePropertiesCommand(File outFile) {
        writeToStringCommand = new WriteToStringCommand();
        macroCommand = new MacroCommand<>(writeToStringCommand, new WriteToFileCommand(outFile));
    }

    public CreateGradlePropertiesCommand addProperty(String key, String value) {
        writeToStringCommand
                .append(key)
                .append("=")
                .append(value)
                .newLine();
        return this;
    }

    public CreateGradlePropertiesCommand addProperty(String key1, String key2, String value) {
        writeToStringCommand
                .append(key1)
                .append(key2)
                .append("=")
                .append(value)
                .newLine();
        return this;
    }

    public CreateGradlePropertiesCommand append(String s) {
        writeToStringCommand.append(s);
        return this;
    }

    public CreateGradlePropertiesCommand newLine() {
        writeToStringCommand.newLine();
        return this;
    }

    @Override
    public File execute(Object data) throws Exception {
        return macroCommand.execute(null);
    }
}
