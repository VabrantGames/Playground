package com.vabrant.playground;

import com.vabrant.playground.commands.*;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

import static com.vabrant.playground.Setup.*;

@CommandLine.Command(
        name = "init",
        description = "Initialize a standalone or libGDX integrated playground project.",
        mixinStandardHelpOptions = true
)
public class Init implements Callable<Integer> {

    @CommandLine.Option(
            names = {"-i", "--integrated"},
            paramLabel = "LibGDX integrated project."
    )
    boolean isIntegrated;

    @CommandLine.Mixin
    private Setup.ShareableOptions options;

    @Override
    public Integer call() throws Exception {
        final String rootDirectory = "root";
        DIRECTORIES.put(rootDirectory, new File(options.playgroundPath));

        File file = DIRECTORIES.get(rootDirectory);

        if (!file.isDirectory())
            throw new RuntimeException("Directory " + file.getAbsolutePath() + " not found.");

        CommandQueue commandQueue = new CommandQueue();

        try {
            if (isIntegrated) {
                File path = DIRECTORIES.get(rootDirectory);

                int found = 0;
                File[] files = path.listFiles();

                if (files == null) {
                    throw new RuntimeException("Not a compatible libGDX project. Directory is empty");
                }

                for (File f : files) {
                    String name = f.getName();
                    if (name.equals("core") || name.equals("settings.gradle")) {
                        found++;
                    }
                }

                if (found < 2) {
                    throw new RuntimeException("Not a compatible libGDX project. Missing core folder and settings.gradle file.");
                }

                path = new File(DIRECTORIES.get(rootDirectory), "playground");
                DIRECTORIES.replace(rootDirectory, path);
                commandQueue.add(new CreateDirectoryCommand(path));
            } else {
                if (!Setup.isEmpty(DIRECTORIES.get(rootDirectory)))
                    throw new RuntimeException("Directory needs to be empty.");
            }

            final String gradleWrapperDirectory = "gradleWrapper";

            commandQueue
                    .add(new CreateDirectoryCommand(new File(getDirectoryFile(rootDirectory), "assets")))
                    .add(new CreateDirectoryCommand(new File(getDirectoryFile(rootDirectory), "playgrounds")))
                    .setTempFile(getResource("/playground").toFile())
                    .add(new CopyFileCommand(new File(commandQueue.getTempFile(), "gitignore"), new File(DIRECTORIES.get(rootDirectory), ".gitignore"))
                            .options(f -> f.setExecutable(true)))
                    .add(new CopyFileCommand(new File(commandQueue.getTempFile(), "gradlew"), new File(DIRECTORIES.get(rootDirectory), "gradlew"))
                            .options(f -> f.setExecutable(true)))
                    .add(new CopyFileCommand(new File(commandQueue.getTempFile(), "gradlew.bat"), new File(DIRECTORIES.get(rootDirectory), "gradlew.bat")))
                    .add(new CreateDirectoryCommand(new File(DIRECTORIES.get(rootDirectory), "gradle/wrapper"))
                            .options(d -> DIRECTORIES.put(gradleWrapperDirectory, d)))
                    .setTempFile(getResource("/playground/gradle/wrapper").toFile())
                    .add(new CopyFileCommand(new File(commandQueue.getTempFile(), "gradle-wrapper.jar"), new File(DIRECTORIES.get(gradleWrapperDirectory), "gradle-wrapper.jar")))
                    .add(new CopyFileCommand(new File(commandQueue.getTempFile(), "gradle-wrapper.properties"), new File(DIRECTORIES.get(gradleWrapperDirectory), "gradle-wrapper.properties")))
                    .add(new MacroCommand()
                            .add(new CopyAndReplaceCommand(getResource("/templates/gradle/RootBuildGradle.txt").toFile(), Setup.createMap(m -> {
                                m.put(Setup.PROJECT_NAME, options.name);
                            })))
                            .add(new CreateFileCommand(new File(DIRECTORIES.get(rootDirectory), "build.gradle"))))
                    .add(new CreateGradlePropertiesCommand(new File(DIRECTORIES.get(rootDirectory), "gradle.properties"))
                            .addProperty("org.gradle.daemon", "true")
                            .addProperty("org.gradle.jvmargs", "-Xms512M -Xmx1G -XX:MaxMetaspaceSize=1G")
                            .addProperty("org.gradle.configureondemand", "false")
                            .addProperty(makeFirstLetterLowerCase(options.name), "Version", "0.0.1"))
                    .add(new MacroCommand()
                            .add(new WriteToStringCommand()
                                    .append("rootProject.name=\"")
                                    .append(options.name)
                                    .append("\"").newLine())
                            .add(new CreateFileCommand(new File(DIRECTORIES.get(rootDirectory), "settings.gradle"))));
//                    .add(new CreateFileCommand);
            commandQueue.execute();

//            File dir = DIRECTORIES.get(rootDirectory);
//            String[] cmds = new String[2];
//            cmds[0] = dir.getAbsolutePath() + "/" + "gradlew.bat";
//            cmds[1] = "hello";
//
//            Process proc = new ProcessBuilder(cmds)
//                    .directory(dir)
//                    .start();
//
//            proc.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        return 0;
    }
}
