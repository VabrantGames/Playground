package com.playground.commands;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ReplaceCommand implements Command<String, Object> {

    private String str;
    private Map<String, String> values;

    public ReplaceCommand(Map<String, String> values) {
        this.values = values;
    }

    @Override
    public String execute(Object data) throws Exception {
        if (data instanceof String) {
            str = (String) data;
        } else if (data instanceof byte[]) {
            str = new String((byte[]) data, StandardCharsets.UTF_8);
        } else {
            throw new RuntimeException("Input not supported");
        }

        if (values == null) return str;

        for (Map.Entry<String, String> s : values.entrySet()) {
            str = str.replace(s.getKey(), s.getValue());
        }
        return str;
    }

}
