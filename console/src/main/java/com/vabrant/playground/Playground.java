package com.vabrant.playground;

import com.vabrant.playground.builders.libgdx.LibgdxBuilder;
import com.vabrant.playground.commands.Command;
import com.vabrant.playground.commands.CreateDirectoryCommand;
import com.vabrant.playground.commands.CreateFileCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Playground {

    public static void main(String[] args) {
        if (args.length == 0) throw new IllegalArgumentException("Invalid arguments.");

        String command = args[0];
        String type = args[2].toLowerCase();

        File rootDirectory = new File(args[1]);
        if (!rootDirectory.isDirectory()) throw new RuntimeException("Not a directory");

        Playground playground = new Playground();
        playground.paths.put(ROOT_PATH, rootDirectory.toPath());
        playground.paths.put(LAUNCHERS_PATH_RELATIVE, Paths.get("/launchers"));

        Builder builder = null;

        switch (type) {
            case "libgdx":
                builder = new LibgdxBuilder();
                break;
            default:
                throw new IllegalArgumentException("");
        }

        switch (command) {
            case "init":
                //TODO For testing purposes
                playground.deleteFilesInDirectory(rootDirectory.toPath());
                playground.init(rootDirectory);
                builder.init(playground);
                break;
            case "create":
                System.out.println("create");
                break;
        }

        playground.execute();
    }

    public static final String ROOT_PATH = "rootpath";
    public static final String LAUNCHERS_PATH_RELATIVE = "launchers";

    private final Deque<Command> commandStack = new ArrayDeque<>();
    private final Map<String, Path> paths = new HashMap<>();

    public void addCommand(Command command) {
        commandStack.push(command);
    }

    void execute() {
        for (Command c : commandStack) {
            try {
                c.execute();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public Path getPath(String name) {
        return paths.get(name);
    }

    public String getPathStr(String name) {
        return paths.get(name).toString();
    }

    //TODO Remove! For testing purposes
    private void deleteFilesInDirectory(Path path) {
        delete(path.toFile());
    }

    private void delete(File file) {
        File[] files = file.listFiles();

        if (files == null) return;

        for (File f : files) {
            delete(f);
            f.delete();
        }
    }

    private void init(File rootDirectory) {
        Path rootPath = rootDirectory.toPath();

        try {
            String[] files = rootDirectory.list();
            if (files != null && files.length == 0) {
                addCommand(new CreateDirectoryCommand(Paths.get(rootPath + "/gradle")));
            }
            else {
                Path integratedPlaygroundPath = Paths.get(rootPath + "/playground");

                if (Files.exists(integratedPlaygroundPath)) throw new RuntimeException("Directory with name Playground already exists.");

                rootPath = Files.createDirectory(integratedPlaygroundPath);
            }

            addCommand(new CreateDirectoryCommand(Paths.get(rootPath + "/assets")));
            addCommand(new CreateDirectoryCommand(Paths.get(rootPath + "/launchers")));
            addCommand(new CreateFileCommand(Paths.get(rootPath + "/build.gradle")));
            addCommand(new CreateFileCommand(Paths.get(rootPath + "/settings.toml")));
            addCommand(new CreateFileCommand(Paths.get(rootPath + "/README.md")));

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void create(String category, String playgroundName) {

    }
}
