package com.vabrant.playground.commands;

import com.vabrant.playground.Headless;

import java.io.*;

public class ReadAsBytesCommand implements Command<byte[], Object> {

    String resource;
    private File file;

    public ReadAsBytesCommand(String resource) {
        this.resource = resource;
    }

    public ReadAsBytesCommand(File file) {
        this.file = file;
    }

    @Override
    public byte[] execute() throws Exception {
        InputStream is = null;

        try {
            if (file != null) {
                is = new FileInputStream(file);
            } else {
                is = Headless.class.getResourceAsStream(resource);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 10];
            int read = 0;
            while ((read = is.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            System.out.println(resource);
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
