package com.vabrant.playground;

import com.vabrant.playground.commands.Command;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;

public class CommandQueue {

    private Path tempPath;
    private File temp;
    private Deque<Command> commands;

    public CommandQueue() {
        commands = new ArrayDeque<>();
    }

    public CommandQueue add(Command command) {
        commands.add(command);
        return this;
    }

    public CommandQueue setTempFile(File temp) {
        this.temp = temp;
        return this;
    }

    public File getTempFile() {
        return temp;
    }

    public CommandQueue setTempPath(Path tempPath) {
        this.tempPath = tempPath;
        return this;
    }

    public Path getTempPath() {
        return tempPath;
    }

    public void execute() throws Exception {
        for (Command c : commands) {
            c.execute();
        }
    }
}
