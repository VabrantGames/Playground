package com.playground.commands;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ReadAsBytesCommand implements Command<byte[], Object> {

    String resource;
    private File file;

    public ReadAsBytesCommand(boolean isResource, String str) {
        if (isResource) {
            this.resource = str;

        } else {
            file = new File(str);
        }
    }

    public ReadAsBytesCommand(File file) {
        this.file = file;
    }

    @Override
    public byte[] execute(Object data) throws Exception {
        InputStream is = null;

        try {
            if (file != null) {
                is = new FileInputStream(file);
            } else {
                is = ReadAsBytesCommand.class.getResourceAsStream(resource);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 10];
            int read = 0;
            while ((read = is.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            String s;
            if (resource != null) {
                s = "Error reading resource: " + resource;
            } else {
                s = "Error reading file: " + file.toString();
            }
//            System.out.println(resource);
            throw new RuntimeException(s, e);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
