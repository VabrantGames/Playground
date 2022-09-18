package com.vabrant.playground.commands;

import java.io.File;

public class CreateGradlePropertiesCommand implements Command<File, Object> {

    private final MacroCommand<File, Object> macro;
    private final WriteToStringCommand writeToStringCommand;

    public CreateGradlePropertiesCommand(File outFile) {
        writeToStringCommand = new WriteToStringCommand();
        macro = new MacroCommand<>(writeToStringCommand, new CreateFileCommand(outFile));
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
    public File execute() throws Exception {
        return macro.execute();
    }
}
