package com.vabrant.playground;

import com.github.tommyettinger.ds.ObjectList;
import com.github.tommyettinger.ds.ObjectObjectMap;
import com.vabrant.playground.commands.*;
import com.vabrant.playground.commands.macro.CopyFileMacroCommand;
import com.vabrant.playground.commands.macro.MacroCommand;
import org.tomlj.TomlArray;
import org.tomlj.TomlTable;
import picocli.CommandLine;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.vabrant.playground.PlaygroundUtils.*;

@CommandLine.Command(
        name = "Playground",
        description = "A playground environment for libgdx projects",
        mixinStandardHelpOptions = true,
        version = "0.1.0")
public class Headless implements Callable<Integer> {

    public static void main(String[] args) {
        String[] input = {"--path", "/volumes/johnshd/developer/java/playgroundoutputtest", "-i", "-n", "Snake", "-p", "Bird:libgdx",
                "-l", "lwjgl3", "-p", "Pizza:libgdx"};

        int exitCode = new CommandLine(new Headless()).execute(args);
        System.exit(exitCode);
    }

    public static final String PLAYGROUND_NAME_TAG = "${PLAYGROUND_NAME}";
    public static final String PLAYGROUND_NAME_LOWERCASE_TAG = "${PLAYGROUND_NAME_LOWERCASE}";
    public static final String PROJECT_NAME_TAG = "${PROJECT_NAME}";
    public static final String PROJECT_NAME_LOWERCASE_TAG = "${PROJECT_NAME_LOWERCASE}";
    public static final String GROUP_TAG = "${GROUP}";

    private Map<String, String> playgroundReplaceMap;

    private File rootDirectory;
    private Playground playground;
    private Settings settings;

    @CommandLine.Option(
            names = "--path",
            description = "Path of the playground")
    String inputDirectory;

    @CommandLine.Option(
            names = "-i",
            description = "Initialize Playground",
    paramLabel = "Hello Playground")
    private boolean initializePlayground;

    @CommandLine.Option(
            names = "-n",
            description = "Name of Playground")
    private String playgroundName;

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "0..*")
    ProjectCommandData[] projectsCommandData;

    private List<Project> projects;

    private final CommandQueue mainCommandQueue = new CommandQueue();
    private CommandQueue commandQueue = mainCommandQueue;

//    @CommandLine.Option(
//            names = "--log",
//            description = "Level of the logger",
//            defaultValue = "INFO")
//    private String logLevelString;

    private int logLevel;

    @Override
    public Integer call() throws Exception {
        if (!new File(inputDirectory).isDirectory()) throw new RuntimeException("No such directory");

        playgroundReplaceMap = new ObjectObjectMap<>();

        logLevel = LOGGER_DEBUG;

        log(logLevel, LOGGER_INFO, "Playground", "VabrantPlayground v0.1.0");

        try {
            rootDirectory = new File(inputDirectory);
            setupPlayground(inputDirectory);
            handleProjects();

            commandQueue.add(new MacroCommand()
                .add(new WriteTomlSettingsCommand(playground, projects, settings))
                .add(new WriteToFileCommand(new File(playground.getPlaygroundDirectory(), "settings.toml"))));
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

        //TODO For standalone projects the root directory should be checked
        boolean isInitialized = playground.getPlaygroundDirectory().isDirectory() && !isDirectoryEmpty(playground.getPlaygroundDirectory());
        File settingsFile = new File(playground.getPlaygroundDirectory(), "settings.toml");

        if (isInitialized) {
            if (settingsFile.exists()) {
                log(logLevel, LOGGER_DEBUG, "Playground", "Loading Settings");
                settings.load(settingsFile);
                playground.setup(settings);
                return;
            } else {
                throw new RuntimeException("Settings.toml not found in root directory.");
            }
        } else {
            if (!initializePlayground) {
                throw new RuntimeException("Playground has not been initialized. Usage: -i");
            } else if (playgroundName == null) {
                throw new RuntimeException("No name specified. Usage: -n <name>");
            } else {
                log(logLevel, LOGGER_INFO, "Playground", "Initializing New Playground");
                settings.loadDefaults();
                playground.setup(playgroundName);
                playground.newPlayground();
            }
        }

        playgroundReplaceMap.put(PLAYGROUND_NAME_TAG, playground.getName());
        playgroundReplaceMap.put(PLAYGROUND_NAME_LOWERCASE_TAG, playground.getNameLowerCase());
        playgroundReplaceMap.put(GROUP_TAG, playground.getGroup());

        //If the playground is new, delete any files create in the event of an error.
        commandQueue.setErrorCallback(new Callback() {
            @Override
            public void onCallback() {
                deleteDirectory(false, playground.getPlaygroundDirectory());
            }
        });

        if (!playground.getPlaygroundDirectory().exists()) {
            commandQueue.add(new CreateDirectoryCommand(playground.getPlaygroundDirectory()));
        }

        commandQueue.add(new CreateDirectoryCommand(playground.getProjectsDirectory()));
        commandQueue.add(new CopyFileMacroCommand(true, "/setup/playground/gradle.properties", new File(playground.getPlaygroundDirectory(), "gradle.properties"), playgroundReplaceMap));
        commandQueue.add(new CopyFileMacroCommand(true, "/setup/playground/buildGradle", new File(playground.getPlaygroundDirectory(), "build.gradle"), playgroundReplaceMap));
        commandQueue.add(new CopyFileMacroCommand(true, "/setup/playground/settings.gradle", new File(playground.getPlaygroundDirectory(), "settings.gradle"), playgroundReplaceMap));
        commandQueue.add(new WriteToFileCommand(new File(playground.getProjectsDirectory(), ".gitkeep")));

    }

    private void handleProjects() throws Exception {
        if (projectsCommandData == null) return;

        projects = new ObjectList<>(5);
        for (ProjectCommandData d : projectsCommandData) {
            CommandQueue queue = new CommandQueue();
            commandQueue = queue;
            Project p = setupProject(d);

            if (p.hasErrors()) continue;

            handleTemplates(p);
            handleLaunchers(d, p);
            projects.add(p);
            queue.setErrorCallback(new ProjectErrorCallback(logLevel, p));
            mainCommandQueue.add(commandQueue);
        }

        commandQueue = mainCommandQueue;
        addProjectsToGradleSettings(projects);
    }

    private Project setupProject(ProjectCommandData projectData) throws Exception {
        char[] nameAsCharArray = projectData.getName().toCharArray();
        if (nameAsCharArray.length == 0 || projectData.getName().isBlank())
            throw new RuntimeException("Project name can't be empty. Usage: -p <name:template>");
        if (nameAsCharArray[0] == ':' || nameAsCharArray[nameAsCharArray.length - 1] == ':')
            throw new RuntimeException("Invalid project name. ':' Can't be at beginning or last index. Usage: -p <name:template>");

        String[] projectNameSplit = splitByChar(nameAsCharArray, ':');
        Project project = new Project(projectNameSplit[0], playground.getProjectsDirectory());

        //Check if a project with the same name was passed in
        for (Project p : projects) {
            if (p.getName().equalsIgnoreCase(projectNameSplit[0])) {
                StringBuilder builder = LOGGER_BUILDER;
                builder.setLength(0);
                builder.append('(');
                builder.append(project.getName());
                builder.append(") Duplicate project passed in");
                log(logLevel, LOGGER_ERROR, "Project", builder.toString());
                project.hasErrors();
                return project;
            }
        }

        //Check if project already exists
        if (project.getRootDirectory().isDirectory() && !isDirectoryEmpty(project.getRootDirectory())) {
            if (!project.getLaunchersDirectory().isDirectory()) {
                StringBuilder builder = LOGGER_BUILDER;
                builder.setLength(0);
                builder.append('(');
                builder.append(project.getName());
                builder.append(") Invalid playground project format. Launchers folder missing");
                log(logLevel, LOGGER_ERROR, "Project", builder.toString());
                project.errors();
                return project;
            }
        } else {
            project.newProject();
        }

        switch (projectNameSplit.length) {
            case 1:
                break;
            case 2:
                project.setPassedInTemplate(projectNameSplit[1]);
                break;
            default:
                throw new RuntimeException("Invalid project name. Too many options. Usage: -p <name:template>");
        }

        Map<String, String> map = new ObjectObjectMap<>(playgroundReplaceMap);
        map.put(PROJECT_NAME_TAG, project.getName());
        map.put(PROJECT_NAME_LOWERCASE_TAG, project.getNameLowerCase());
        map.put(GROUP_TAG, playground.getGroup() + '.' + project.getNameLowerCase());
        project.setReplaceMap(map);

        if (project.isNewProject()) {
            project.createSourceDirectory(playground);

            StringBuilder builder = LOGGER_BUILDER;
            builder.setLength(0);
            builder.append('(');
            builder.append(project.getName());
            builder.append(") New");
            log(logLevel, LOGGER_INFO, "Project", builder.toString());

            commandQueue
                    .add(new CreateDirectoryCommand(project.getRootDirectory()))
                    .add(new CreateDirectoryCommand(project.getSourceDirectory()))
                    .add(new CreateDirectoryCommand(project.getLaunchersDirectory()))
                    .add(new WriteToFileCommand(new File(project.getLaunchersDirectory(), ".gitkeep")));
        } else {
            StringBuilder builder = LOGGER_BUILDER;
            builder.setLength(0);
            builder.append('(');
            builder.append(project.getName());
            builder.append(") Existing");
            log(logLevel, LOGGER_INFO, "Project", builder.toString());
        }

        return project;
    }

    private void handleTemplates(Project project) throws Exception {
        if (project.getPassedInTemplate() == null) return;

        TomlTable templateTable = null;

        for (String s : settings.getTemplateNames()) {
            if (s.equalsIgnoreCase(project.getPassedInTemplate())) {
                templateTable = settings.getTemplatesTable().getTable(s);
            }
        }

        if (templateTable == null) {
            throw new RuntimeException("Template '" + project.getPassedInTemplate() + "' not found. Available Templates:" + prettyPrintSet(false, settings.getTemplateNames()));
        }

        boolean isResource = Boolean.TRUE.equals(templateTable.getBoolean("isResource"));
        String baseFilePath = null;

        if (isResource) {
            baseFilePath = "/templates/" + project.getPassedInTemplate();
        } else {
            //For external templates created by user
        }

        queueDirectoryToCopy(isResource, project.getRootDirectory(), templateTable.getTable("root"), baseFilePath + "/root/", project.getReplaceMap(), null);
        queueDirectoryToCopy(isResource, project.getSourceDirectory(), templateTable.getTable("source"), baseFilePath + "/source/", project.getReplaceMap(), new String[]{"Template.java", project.getName() + ".java"});
    }

    private void handleLaunchers(ProjectCommandData data, Project project) throws Exception {
        if (data.getLaunchers() == null) return;

        TomlTable allLaunchersTable = settings.getLaunchersTable();

        for (String passedInLauncher : data.getLaunchers()) {
            TomlTable launcherTable = null;
            String launcherName = null;

            for (String s : settings.getLauncherNames()) {
                if (s.equalsIgnoreCase(passedInLauncher)) {
                    launcherTable = allLaunchersTable.getTable(s);
                    launcherName = s;
                }
            }

            if (launcherTable == null) {
                StringBuilder builder = LOGGER_BUILDER;
                builder.setLength(0);
                builder
                        .append('(')
                        .append(project.getName())
                        .append(") Launcher '")
                        .append(passedInLauncher)
                        .append("' not found.");
                log(logLevel, LOGGER_ERROR, "Project", builder.toString());
                continue;
            }

            String launcherNameLowerCase = launcherName.toLowerCase();
            File rootDirectory = new File(project.getLaunchersDirectory(), launcherNameLowerCase);

            if (rootDirectory.isDirectory() && !isDirectoryEmpty(rootDirectory)) {
                StringBuilder builder = LOGGER_BUILDER;
                builder.setLength(0);
                builder
                        .append('(')
                        .append(project.getName())
                        .append(") Directory '")
                        .append(launcherNameLowerCase)
                        .append("' already exists.");
                log(logLevel, LOGGER_ERROR, "Project", builder.toString());
                continue;
            }

            String group = project.getReplaceMap().get(GROUP_TAG).replace('.', '/') + "/launchers/" + launcherNameLowerCase;
            File sourceDirectory = new File(rootDirectory, "src/main/java/" + group);
            boolean isResource = Boolean.TRUE.equals(launcherTable.getBoolean("isResource"));

            commandQueue.add(new CreateDirectoryCommand(rootDirectory));
            commandQueue.add(new CreateDirectoryCommand(sourceDirectory));

            String baseFilePath = null;

            if (isResource) {
                baseFilePath = "/launchers/" + launcherNameLowerCase;
            } else {
                //For external launchers created by the user
            }

            queueDirectoryToCopy(isResource, rootDirectory, launcherTable.getTable("root"), baseFilePath + "/root/", project.getReplaceMap(), null);
            queueDirectoryToCopy(isResource, sourceDirectory, launcherTable.getTable("source"), baseFilePath + "/source/", project.getReplaceMap(), new String[]{"Launcher.java", project.getName() + launcherName + "Launcher.java"});

            project.addLauncher(launcherName);
        }
    }

    private void queueDirectoryToCopy(boolean isResource, File d, TomlTable table, String baseFilePath, Map<String, String> replaceMap, String[] mainFileNameReplace) {
        table.entrySet().forEach(e -> {
            String key = e.getKey();
            TomlArray value = (TomlArray) e.getValue();

            File directory;
            if (key.equals("")) {
                directory = d;
            } else {
                directory = new File(d, key);
                commandQueue.add(new CreateDirectoryCommand(directory));
            }

            List<String> list = (List) value.toList();
            for (String fileName : list) {
                String filePath = baseFilePath;

                if (key.equals("")) {
                    filePath += fileName;
                } else {
                    filePath += key + "/" + fileName;
                }

                //Replace the 'Entry' file.
                if (mainFileNameReplace != null && fileName.equals(mainFileNameReplace[0])) {
                    fileName = mainFileNameReplace[1];
                }

                commandQueue.add(new CopyFileMacroCommand(isResource, filePath, new File(directory, fileName), replaceMap));
            }
        });
    }

    private void addProjectsToGradleSettings(List<Project> projects) throws Exception {
        File file = new File(playground.getPlaygroundDirectory(), "settings.gradle");
        commandQueue.add(new MacroCommand()
                .add(new ReadAsBytesCommand(file))
                .add(new ChangeGradleSettingsCommand(projects))
                .add(new WriteToFileCommand(file)));
    }

}
