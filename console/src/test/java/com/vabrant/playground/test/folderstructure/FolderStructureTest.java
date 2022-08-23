package com.vabrant.playground.test.folderstructure;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FolderStructureTest {

    private String testPath = "src/test/java/com/vabrant/playground/test/folderstructure";

    @Test
    @Disabled
    public void createStructureTest() {
        String fullPath = System.getProperty("user.dir") + testPath;
        File rootFolder = new File(fullPath + "/playground");

        //Delete existing folders
        File[] files = rootFolder.listFiles();
        for (File f : files) {
            f.delete();
        }
        rootFolder.delete();

        if (!rootFolder.mkdir()) throw new RuntimeException("Unable to create folder.");

        createPlayground(rootFolder.getAbsolutePath(), "animation");
        createPlayground(rootFolder.getAbsolutePath(), "board");
    }

    private void createPlayground(String rootPath, String playgroundName) {
        File playground = new File(rootPath + '/' + playgroundName);
        playground.mkdir();
    }

    @Test
    public void createStructureTest2() {
        try {
            Files.deleteIfExists(Paths.get(testPath + "/playground2/animation"));
            Files.deleteIfExists(Paths.get(testPath + "/playground2"));
            Path p = Files.createDirectories(Paths.get(testPath + "/playground2/animation"));
            System.out.println(Files.exists(p));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
