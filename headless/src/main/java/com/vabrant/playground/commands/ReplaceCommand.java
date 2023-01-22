package com.vabrant.playground.commands;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ReplaceCommand implements Command<String, Object> {

    private String str;
    private Map<String, String> values;
//    private BiFunction<String, String, String> replaceFunction;

    public ReplaceCommand(Map<String, String> values) {
        this.values = values;
    }

    @Override
    public void setData(Object data) {
        if (data instanceof String) {
            str = (String) data;
        } else if (data instanceof byte[]) {
            str = new String((byte[]) data, StandardCharsets.UTF_8);
        } else {
            throw new RuntimeException("Input not supported");
        }
//        System.out.println("Hello World");
//        str = data;
    }

//    public ReplaceCommand setReplaceFunction(BiFunction<String, String, String> function) {
//        this.replaceFunction = function;
//        return this;
//    }

    @Override
    public String execute() throws Exception {
        for (Map.Entry<String, String> s : values.entrySet()) {
            str = str.replace(s.getKey(), s.getValue());
//            str = replaceFunction.apply(s.getKey(), s.getValue());
        }
        return str;
    }

}
