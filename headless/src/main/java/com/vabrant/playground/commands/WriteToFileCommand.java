package com.vabrant.playground.commands;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class WriteToFileCommand implements Command<File, Object> {

    private File file;
    private byte[] bytes;

    public WriteToFileCommand(File file) {
        this.file = file;
    }

    @Override
    public void setData(Object data) {
        if (data instanceof String) {
            bytes = ((String) data).getBytes(StandardCharsets.UTF_8);
        } else {
            bytes = (byte[]) data;
        }
    }

    @Override
    public File execute() throws Exception {
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
            if (bytes != null) {
                os.write(bytes);
                os.flush();
            }
            return file;
        }
    }
}
