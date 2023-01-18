package com.vabrant.playground;

import com.vabrant.playground.commands.*;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;
import picocli.CommandLine;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

@CommandLine.Command(
        name = "Playground")
public class Headless implements Callable<Integer> {

    public static void main(String[] args) {

        String[] input = {"--path", "/volumes/johnshd/developer/java/playgroundoutputtest", "-i", "-n", "Snake", "-p", "Bird:templatetest",
                "-l", "lwjgl3"};
//        String[] input = {"--path", "/volumes/johnshd/developer/java/playgroundoutputtest", "-p", "hi"};
        String[] project = {" ", "-p", "hello"};

        int exitCode = new CommandLine(new Headless()).execute(input);
        System.exit(exitCode);
//        try {
////            URLClassLoader.newInstance()
////            System.out.println(URLClassLoader.getSystemResource("templates/default/root/").getProtocol());
////            System.out.println(URLClassLoader.getSystemResource("templates/default/root/buildGradle/").getProtocol());
//            System.out.println(Headless.class.getResource("/templates/default/roo/") == null);
//            System.out.println(Headless.class.getResource("/templates/default/root/buildGradle/") == null);
//
//            InputStream is = Headless.class.getResourceAsStream("/templates/default/root");
//            System.out.println(is.toString());
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
//
//            String line;
//            while ((line = br.readLine()) != null) {
//                try {
//                    InputStream is2 = Headless.class.getResourceAsStream("/templates/default/root/" + line);
//                    BufferedReader br2 = new BufferedReader(new InputStreamReader(is2, StandardCharsets.UTF_8));
//
//                    String s;
//                    while ((s = br2.readLine()) != null) {
//                        System.out.println(s);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    System.exit(-1);
//                }
//            }
//
//            System.out.println("Null: " + is == null);
//
//            if (is != null) is.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.exit(-1);
//        }
    }

    private final String PLAYGROUND_NAME = "${PLAYGROUND_NAME}";
    private final String PLAYGROUND_NAME_LOWERCASE = "${PLAYGROUND_NAME_LOWERCASE}";
    private final String GROUP_NAME = "${GROUP_NAME}";
    private final String PROJECT_NAME = "${PROJECT_NAME}";
    private final String PROJECT_NAME_LOWERCASE = "${PROJECT_NAME_LOWERCASE}";
    private final String GROUP = "${GROUP}";

    private String[] launchers = {"lwjgl3"};

    private File rootDirectory;
    private Playground playground;
    private Settings settings;

    @CommandLine.Option(names = {"--path"})
    String inputDirectory;

//    @CommandLine.Option(names = {"-i", "--init"})
//    boolean initializePlayground;

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "0..1")
    PlaygroundCommandData playgroundCommandData;

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "0..*")
    ProjectCommandData[] projectsCommandData;

    private final CommandQueue commandQueue = new CommandQueue();

    @Override
    public Integer call() throws Exception {
        if (!new File(inputDirectory).isDirectory()) throw new RuntimeException("No such directory");

        rootDirectory = new File(inputDirectory);

        try {
            setupPlayground(inputDirectory);
            checkProjectData();
            handleProjects();

//            commandQueue.execute();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public boolean doesLauncherExist(String launcher) {
        for (String s : launchers) {
            if (s.equalsIgnoreCase(launcher)) return true;
        }
        return false;
    }

    Map<String, String> createMap(Consumer<Map<String, String>> c) {
        Map<String, String> map = new HashMap();
        c.accept(map);
        return map;
    }

    private void setupPlayground(String path) throws Exception {
        playground = new Playground(rootDirectory);

        boolean isInitialized = playground.getPlaygroundDirectory().isDirectory();

        if (isInitialized) {
            File settingsFile = new File(playground.getPlaygroundDirectory(), "settings.toml");

            if (settingsFile.exists()) {
                settings = new Settings(settingsFile);
                return;
            } else {
                throw new RuntimeException("Settings file not found");
            }
        } else if (!isInitialized) {
            if (playgroundCommandData == null || !playgroundCommandData.initialize()) {
                throw new RuntimeException("Playground has not been initialized.");
            }
        }

        commandQueue.add(new CreateDirectoryCommand(playground.getPlaygroundDirectory()));
        commandQueue.add(new CreateDirectoryCommand(playground.getProjectsDirectory()));
        commandQueue.add(new MacroCommand()
                .add(new ReadAsBytesCommand("/setup/playground/buildGradle"))
                .add(new ReplaceCommand(createMap(m -> {
                    m.put(PLAYGROUND_NAME, playground.getName());
                    m.put(PLAYGROUND_NAME_LOWERCASE, playground.getNameLowerCase());
                    m.put(GROUP, playground.getGroup() + '.' + playground.getNameLowerCase());
                })))
                .add(new WriteToFileCommand(new File(playground.getPlaygroundDirectory(), "build.gradle"))));
        commandQueue.add(new MacroCommand()
                .add(new ReadAsBytesCommand("/setup/playground/settings.gradle"))
                .add(new ReplaceCommand(createMap(m -> {
                    m.put(PLAYGROUND_NAME, playground.getName());
                })))
                .add(new WriteToFileCommand(new File(playground.getPlaygroundDirectory(), "settings.gradle"))));
        commandQueue.add(new WriteToFileCommand(new File(playground.getProjectsDirectory(), ".gitkeep")));
    }

    //Ensure the options that were passed in are real
    public void checkProjectData() {
        if (projectsCommandData == null) return;

        for (ProjectCommandData d : projectsCommandData) {
            //Check if the playground has a folder with the name

            if (d.launchers != null) {
                for (String s : d.launchers) {
                    if (!doesLauncherExist(s)) throw new RuntimeException("Launcher not found. Name: " + s);
                }
            }
        }
    }

    private void handleProjects() throws Exception {
        if (projectsCommandData == null) return;

        for (ProjectCommandData d : projectsCommandData) {
            setupProject(d);
        }


    }

    private void setupProject(ProjectCommandData projectData) throws Exception {
        char[] nameAsCharArray = projectData.getName().toCharArray();
        if (nameAsCharArray.length == 0 || projectData.getName().isBlank())
            throw new RuntimeException("Project name can't be empty");
        if (nameAsCharArray[0] == ':' || nameAsCharArray[nameAsCharArray.length - 1] == ':')
            throw new RuntimeException("Invalid project name.");

        String[] projectNameSplit = Utils.splitByChar(nameAsCharArray, ':');
        if (projectNameSplit.length == 2) {
            projectData.setTemplateString(projectNameSplit[1]);
        } else {
            throw new RuntimeException("Invalid project name. Too many options.");
        }

        boolean newProject = false;
        Project project = new Project(projectNameSplit[0], playground.getProjectsDirectory());

        if (settings != null && settings.hasProjectName(projectNameSplit[0])) {
            project.setup(playground, settings.getLaunchers(projectNameSplit[0]));
        } else {
            newProject = true;
            project.setup(playground);
        }

        if (newProject) {
            project.setNewProject();
            System.out.println("[New Project] " + projectNameSplit[0]);

            commandQueue
                    .add(new CreateDirectoryCommand(project.getRootDirectory()))
                    .add(new CreateDirectoryCommand(project.getSourceDirectory()))
                    .add(new CreateDirectoryCommand(project.getLaunchersDirectory()))
                    .add(new CreateDirectoryCommand(new File(project.getLaunchersDirectory(), ".gitkeep")));
        }
    }

    private void handleTemplates(ProjectCommandData projectData, Project project) throws Exception {
        if (projectData.getTemplateString() == null) return;

        Map<String, Map<String, Object>> templateSettings = settings.getTemplatesMap();

        if (playground.getSettings() == null) {
//            Toml defaultTemplates = new Toml().read(Headless.class.getResourceAsStream("/setup/templates.toml"));
//            templateSettings = (Map) defaultTemplates.toMap().get("templatetest");
        }

        //Root stuff
        Map<String, Map<String, ArrayList<String>>> root = (Map) templateSettings.get("root");
        for (String s : root.keySet()) {
            File dir = null;

            if (s == "") {
                dir = project.getRootDirectory();
            } else {
                dir = new File(project.getRootDirectory(), s.replace("\"", ""));
                commandQueue.add(new CreateDirectoryCommand(dir));
            }

            ArrayList<String> files = (ArrayList) root.get(s);
            for (String ss : files) {
                File f = new File(dir, ss);
                System.out.println("\t" + f.toString());
            }
        }

        //Source stuff
        Map<String, Map<String, ArrayList<String>>> source = (Map) templateSettings.get("source");
        for (String s : source.keySet()) {
            File dir = null;

            if (s == "") {
                dir = project.getSourceDirectory();
            } else {
                dir = new File(project.getSourceDirectory(), s.replace("\"", ""));
                commandQueue.add(new CreateDirectoryCommand(dir));
            }

            ArrayList<String> files = (ArrayList) source.get(s);
            for (String ss : files) {
                if (ss.equals("main_java")) {
                    ss = project.getName() + ".java";
                }

                File f = new File(dir, ss);
                System.out.println("\t" + f.toString());
            }
        }
    }

    private boolean isResourceAFile(String resource) {
        return Headless.class.getResource(resource + '\\') == null;
    }

    private void handleLaunchers(ProjectCommandData data, Project project) {
        for (String s : data.getLaunchers()) {
            File launcherDirectory = new File(project.getLaunchersDirectory(), s);
            File sourceDirectory = new File(launcherDirectory, "src/main/java/com/playground/" + project.getNameLowerCase() + '/' + s);
            commandQueue.add(new CreateDirectoryCommand(sourceDirectory));
            commandQueue.add(new MacroCommand()
                    .add(new ReadAsBytesCommand("/launchers/lwjgl3/source/Lwjgl3Launcher"))
                    .add(new ReplaceCommand(createMap(m -> {
                        m.put(PROJECT_NAME, project.getName());
                        m.put(PROJECT_NAME_LOWERCASE, project.getNameLowerCase());
                    })))
                    .add(new WriteToFileCommand(new File(sourceDirectory, "Lwjgl3Launcher.java"))));
        }
    }

    private void addProjectsToGradleSettings() throws Exception {
        File gradleSettings = new File(playground.getPlaygroundDirectory(), "settings.gradle");
        commandQueue.add(new WriteToSettingsFileCommand(gradleSettings, projectsCommandData));
//        WriteToSettingsFileCommand c = new WriteToSettingsFileCommand(gradleSettings, projects);
//        c.execute();
    }

}
