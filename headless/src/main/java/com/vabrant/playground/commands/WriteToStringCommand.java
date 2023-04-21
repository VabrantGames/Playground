package com.vabrant.playground.commands;

import com.playground.commands.Command;

public class WriteToStringCommand implements Command<String, Object> {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private final StringBuilder builder;

    public WriteToStringCommand() {
        builder = new StringBuilder();
    }

    public StringBuilder getBuilder() {
        return builder;
    }

    public WriteToStringCommand append(String s) {
        builder.append(s);
        return this;
    }

    public WriteToStringCommand newLine() {
        builder.append(LINE_SEPARATOR);
        return this;
    }

    @Override
    public String execute(Object data) throws Exception {
        return builder.toString();
    }
}
