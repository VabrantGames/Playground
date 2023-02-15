package com.vabrant.playground.commands;

import java.io.File;

public class DirectoryInfo {

    private String dirs;
    private File rootFile;
    private File file;

    public DirectoryInfo(File file, File rootFile, String dirs) {
        this.file = file;
        this.rootFile = rootFile;
        this.dirs = dirs;
    }

    public String getDirs() {
        return dirs;
    }

    public File getFile() {
        return file;
    }

    public File getRootFile() {
        return rootFile;
    }
}
