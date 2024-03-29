package com.playground.commands;

import com.github.tommyettinger.ds.ObjectList;

import java.util.Arrays;
import java.util.List;

public class MacroCommand<T, U> implements Command<T, U> {

    private int idx = -1;
    private U data;
    private final List<Command> commands;

    public MacroCommand(Command... c) {
        commands = new ObjectList<>();
        if (c != null) commands.addAll(Arrays.asList(c));
    }

    public MacroCommand<T, U> add(Command command) {
        commands.add(command);
        return this;
    }

    @Override
    public T execute(U data) throws Exception {
        Object output = null;

        for (int i = 0, n = commands.size(); i < n; i++) {
            idx++;
            output = commands.get(i).execute(output);
        }

        return (T) output;
    }
}
