package com.vabrant.playground.commands;

import com.vabrant.playground.Setup;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class CreateFileCommand implements Command<File, String> {

    private final File outFile;
    private Consumer<File> options;
    private byte[] bytes;

    public CreateFileCommand(File outFile, byte[] bytes) {
        this(outFile);
        this.bytes = bytes;
    }

    public CreateFileCommand(File outFile) {
        this.outFile = outFile;
    }

    public CreateFileCommand options(Consumer<File> file) {
        options = file;
        return this;
    }

    @Override
    public File execute() throws Exception {
        if (bytes != null) {
            Setup.write(outFile, bytes);
        }
        else {

//            throw new UnsupportedOperationException("Why didn't I implement this?");
        }

        if (options != null) options.accept(outFile);

        return outFile;
    }

    @Override
    public void setData(String data) {
        bytes = data.getBytes(StandardCharsets.UTF_8);
    }
}
