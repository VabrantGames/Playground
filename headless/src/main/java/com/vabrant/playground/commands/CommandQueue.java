package com.vabrant.playground.commands;

import com.github.tommyettinger.ds.ObjectDeque;
import com.vabrant.playground.Callback;

import java.io.File;
import java.nio.file.Path;

public class CommandQueue implements Command<Object, Object> {

    private Path tempPath;
    private File temp;
    private ObjectDeque<Command> commands;
    private Callback errorCallback;

    public CommandQueue() {
        commands = new ObjectDeque<>();
    }

    public void setErrorCallback(Callback callback) {
        errorCallback = callback;
    }

    public CommandQueue add(Command<?, ?> command) {
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

    public Object execute(Object data) {
        int idx = -1;
        try {
            for (Command c : commands) {
                idx++;
                c.execute(null);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (errorCallback != null) errorCallback.onCallback();
        }
        return null;
    }
}
