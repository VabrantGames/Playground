package com.vabrant.playground;

import com.vabrant.playground.commands.*;
import com.vabrant.playground.commands.macro.MacroCommand;
import picocli.CommandLine;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.vabrant.playground.PlaygroundUtils.*;

@CommandLine.Command(
        name = "Playground")
public class Headless implements Callable<Integer> {

    public static void main(String[] args) {

//        String[] input = {"--path", "/volumes/johnshd/developer/java/playgroundoutputtest", "-i", "-n", "Snake", "-p", "Bird:templatetest",
//                "-l", "lwjgl3"};
//        String[] input = {"--path", "/volumes/johnshd/developer/java/playgroundoutputtest", "-in", "MyPlayground", "-p", "FirstProject"};
        String[] input = {"--path", "/volumes/johnshd/developer/java/playgroundoutputtest", "-in", "MyPlayground", "-p", "HelloWorld"};
        String[] project = {" ", "-p", "hello"};

        int exitCode = new CommandLine(new Headless()).execute(input);
        System.exit(exitCode);
    }

    private final String PLAYGROUND_NAME = "${PLAYGROUND_NAME}";
    private final String PLAYGROUND_NAME_LOWERCASE = "${PLAYGROUND_NAME_LOWERCASE}";
    private final String GROUP_NAME = "${GROUP_NAME}";
    private final String PROJECT_NAME = "${PROJECT_NAME}";
    private final String PROJECT_NAME_LOWERCASE = "${PROJECT_NAME_LOWERCASE}";
    private final String GROUP = "${GROUP}";

    private File rootDirectory;
    private Playground playground;
    private Settings settings;

    @CommandLine.Option(names = {"--path"})
    String inputDirectory;

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "0..1")
    PlaygroundCommandData playgroundCommandData;

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "0..*")
    ProjectCommandData[] projectsCommandData;

    private final CommandQueue mainCommandQueue = new CommandQueue();
    private CommandQueue commandQueue = mainCommandQueue;

    @Override
    public Integer call() throws Exception {
        if (!new File(inputDirectory).isDirectory()) throw new RuntimeException("No such directory");

        rootDirectory = new File(inputDirectory);

        try {
            setupPlayground(inputDirectory);
            handleProjects();
            commandQueue.execute(null);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    private void setupPlayground(String path) throws Exception {
        playground = new Playground(rootDirectory);
        settings = new Settings();

        boolean isInitialized = playground.getPlaygroundDirectory().isDirectory();
        File settingsFile = new File(playground.getPlaygroundDirectory(), "settings.toml");

        if (isInitialized) {

            if (settingsFile.exists()) {
                settings.load(settingsFile);
                return;
            } else {
                throw new RuntimeException("Settings file not found");
            }
        } else if (!isInitialized) {
            if (playgroundCommandData == null || !playgroundCommandData.initialize()) {
                throw new RuntimeException("Playground has not been initialized. Usage: -i");
            } else if (playgroundCommandData.getName() == null) {
                throw new RuntimeException("No name specified. Usage: -n <name>");
            } else {
                playground.setup(playgroundCommandData);
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
        commandQueue.add(new MacroCommand()
                .add(new ReadAsBytesCommand("/setup/playground/settings_toml"))
                .add(new ReplaceCommand(createMap(m -> {
                    m.put(PLAYGROUND_NAME, playground.getName());
                    m.put(PLAYGROUND_NAME_LOWERCASE, playground.getNameLowerCase());
                    m.put(GROUP, playground.getGroup());
                })))
                .add(new WriteToFileCommand(settingsFile)));
    }

    private void handleProjects() throws Exception {
        if (projectsCommandData == null) return;

        ArrayList<Project> projects = new ArrayList<>();
        for (ProjectCommandData d : projectsCommandData) {
            CommandQueue queue = new CommandQueue();
            commandQueue = queue;
            Project p = setupProject(d);
            projects.add(p);

            queue.setErrorCallback(new ProjectErrorCallback(p));
            mainCommandQueue.add(commandQueue);
        }

        commandQueue = mainCommandQueue;
        addProjectsToGradleSettings(projects);
        updateSettings(projects);
    }

    private Project setupProject(ProjectCommandData projectData) throws Exception {
        char[] nameAsCharArray = projectData.getName().toCharArray();
        if (nameAsCharArray.length == 0 || projectData.getName().isBlank())
            throw new RuntimeException("Project name can't be empty.");
        if (nameAsCharArray[0] == ':' || nameAsCharArray[nameAsCharArray.length - 1] == ':')
            throw new RuntimeException("Invalid project name. ':' Can't be at beginning or last index.");

        String[] projectNameSplit = PlaygroundUtils.splitByChar(nameAsCharArray, ':');
        switch (projectNameSplit.length) {
            case 1:
                break;
            case 2:
                projectData.setTemplateString(projectNameSplit[1]);
                break;
            default:
                throw new RuntimeException("Invalid project name. Too many options.");
        }

        boolean newProject = true;
        Project project = new Project(projectNameSplit[0], playground.getProjectsDirectory());

        //TODO Check if passed in launchers exist

        if (settings != null && settings.hasProjectName(projectNameSplit[0])) {
        } else {
            newProject = true;
        }

        if (newProject) {
            project.setNewProject();
            project.createSourceAndLaunchersDirectory(playground);
            System.out.println("New Project Added: " + projectNameSplit[0]);

            commandQueue
                    .add(new CreateDirectoryCommand(project.getRootDirectory()))
                    .add(new CreateDirectoryCommand(project.getRootDirectory(), "src/main/java"))
//                    .add(new CreateDirectoryCommand(project.getSourceDirectory()))
                    .add(new CreateDirectoryCommand(project.getLaunchersDirectory()))
                    .add(new WriteToFileCommand(new File(project.getLaunchersDirectory(), ".gitkeep")));
            commandQueue.add(new Command() {
                @Override
                public Object execute(Object object) {
                    System.out.println("Is this ran?");
                    throw new RuntimeException("Problem with project but not playground");
                }
            });
        }

        return project;
    }

    private void handleTemplates(ProjectCommandData projectData, Project project) throws Exception {
        if (projectData.getTemplateString() == null) return;

        Map<String, Map<String, Object>> templateSettings = (Map) settings.getTemplatesMap();

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

    private void addProjectsToGradleSettings(ArrayList<Project> projects) throws Exception {
        File file = new File(playground.getPlaygroundDirectory(), "settings.gradle");
        commandQueue.add(new MacroCommand()
                .add(new ReadAsBytesCommand(file))
                .add(new ChangeGradleSettingsCommand(projects))
                .add(new WriteToFileCommand(file)));
    }

    private void updateSettings(ArrayList<Project> projects) {
        File file = new File(playground.getPlaygroundDirectory(), "settings.toml");
        commandQueue.add(new MacroCommand()
                .add(new ReadAsBytesCommand(file))
                .add(new ChangeSettingsFileCommand(settings, projects))
                .add(new WriteToFileCommand(file)));
    }

}
