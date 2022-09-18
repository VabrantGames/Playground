package com.vabrant.playground.commands;

import com.vabrant.playground.Setup;

import java.io.File;
import java.util.Map;

public class CopyAndReplaceCommand implements Command<String, Object> {

    private final File file;
    private final Map<String, String> values;

    public CopyAndReplaceCommand(File file, Map<String, String> values) {
        this.file = file;
        this.values = values;
    }

    @Override
    public String execute() throws Exception {
        String str = Setup.readAsString(file);
        for (Map.Entry<String, String> e : values.entrySet()) {
            str = str.replace(e.getKey(), e.getValue());
        }
        return str;
    }
}
