package com.vabrant.playground.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MacroCommand<T, U> implements Command<T, U> {

    private U data;
    private final List<Command> commands;

    public MacroCommand(Command<?, ?>... c) {
        commands = new ArrayList<>();
        if (c != null) commands.addAll(Arrays.asList(c));
    }

    public MacroCommand add(Command command) {
        commands.add(command);
        return this;
    }

    @Override
    public void setData(U data) {
        this.data = data;
    }

    @Override
    public T execute() throws Exception {
        Object output = null;

//        {
//            Command c = commands.get(0);
//            c.setData(data);
//        }

        if (data != null) commands.get(0).setData(data);

        for (int i = 0, n = commands.size(); i < n; i++) {
            Command curr = commands.get(i);
            Command next = (i + 1) < n ? commands.get(i + 1) : null;

            output = curr.execute();
            if (next != null) next.setData(output);
        }

        return (T) output;
    }
}
