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
    public static final String INTEGRATED_DEPENDENCY_TAG = "${INTEGRATED_DEPENDENCY}";
    public static final String USE_GLOBAL_ASSETS_TAG = "${USE_GLOBAL_ASSETS}";
    public static final String GLOBAL_ASSETS_PATH_TAG = "${GLOBAL_ASSETS_PATH}";
    public static final String ROOT_PROJECT_NAME_TAG = "${ROOT_PROJECT_NAME}";

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
    private String initializePlayground;

//    @CommandLine.Option(
//            names = "-n",
//            description = "Name of Playground")
//    private String playgroundName;

    @CommandLine.Option(names = "--standalone")
    boolean isStandalone;

    @CommandLine.Option(names = "-f")
    boolean force;

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "0..*")
    ProjectCommandData[] projectsCommandData;

    private List<Project> projects;

    private final CommandQueue mainCommandQueue = new CommandQueue();
    private CommandQueue commandQueue = mainCommandQueue;

    private LogLevel logLevel;

    @Override
    public Integer call() throws Exception {
        if (!new File(inputDirectory).isDirectory()) throw new RuntimeException("No such input directory");

        playgroundReplaceMap = new ObjectObjectMap<>();

        logLevel = LogLevel.DEBUG;

        log(logLevel, LogLevel.INFO, null, "\n#---------- VabrantPlayground v0.1.0 ----------#");

        try {
            rootDirectory = new File(inputDirectory);
            setupPlayground(inputDirectory);
            handleProjects();

            commandQueue.add(new MacroCommand()
                    .add(new WriteTomlSettingsCommand(playground, projects, settings))
                    .add(new WriteToFileCommand(new File(playground.getRootDirectory(), "settings.toml"))));
            commandQueue.execute(null);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    private void setupPlayground(String path) throws Exception {
//        playground = new Playground(rootDirectory);
        playground = null;
        settings = new Settings();

        File playgroundDirectory = new File(rootDirectory, "playground");

        if (initializePlayground != null) {
            if (initializePlayground.isEmpty()) throw new RuntimeException("Invalid playground name. Usage: -i <name>");

            if (isStandalone) {
                if (!isDirectoryEmpty(rootDirectory) && !force)
                    throw new RuntimeException("Standalone playgrounds must be created in an empty directory");
                playground = new Playground(rootDirectory, playgroundDirectory);
                playground.setStandalone();
            } else {
                playground = new Playground(playgroundDirectory, playgroundDirectory);
                if (playground.getPlaygroundDirectory().exists() && !isDirectoryEmpty(playground.getPlaygroundDirectory()))
                    throw new RuntimeException();
            }

            log(logLevel, LogLevel.DEBUG, "Playground", "New");
            settings.loadDefaults();
            playground.setup(initializePlayground);
            playground.newPlayground();
        } else {
            boolean isStandalone = false;
            File settingsFile = new File(rootDirectory, "settings.toml");

            //Look for settings.toml file in root directory and playground directory
            if (!settingsFile.exists()) {
                settingsFile = new File(playgroundDirectory, "settings.toml");

                if (!settingsFile.exists()) throw new RuntimeException("Settings.toml not found");
            } else {
                isStandalone = true;
            }

            if (isStandalone) {
                playground = new Playground(rootDirectory, playgroundDirectory);
                playground.setStandalone();
            } else {
                playground = new Playground(playgroundDirectory, playgroundDirectory);
            }

            settings.load(settingsFile);
            playground.setup(settings);
            log(logLevel, LogLevel.DEBUG, "Playground", "Settings loaded");
            return;
        }

//        boolean doesSettingsFileExist = false;
//        File settingsFile = new File(playground.getPlaygroundDirectory(), "settings.toml");
//
//        if (settingsFile.exists()) {
//            if (initializePlayground != null) {
//                throw new RuntimeException("Playground already initialized");
//            }
//
//            settings.load(settingsFile);
//            log(logLevel, LogLevel.DEBUG, "Playground", "Settings loaded");
//            playground.setup(settings);
//            return;
//        } else if (initializePlayground != null && !initializePlayground.isEmpty()) {
//            if (isStandalone && !isDirectoryEmpty(rootDirectory) && !force) {
//                throw new RuntimeException("Standalone playgrounds must be created in an empty directory");
//            } else if (force && playground.getPlaygroundDirectory().isDirectory() || force && new File(rootDirectory, "gradle").isDirectory()) {
//                throw new RuntimeException("Playground directory already exists");
//            }
//
//            log(logLevel, LogLevel.DEBUG, "Playground", "New");
//            settings.loadDefaults();
//            playground.setup(initializePlayground);
//            playground.newPlayground();
//        } else {
//            throw new RuntimeException("Playground has not been initialized. Usage: -i <name>");
//        }

        playgroundReplaceMap.put(PLAYGROUND_NAME_TAG, playground.getName());
        playgroundReplaceMap.put(PLAYGROUND_NAME_LOWERCASE_TAG, playground.getNameLowerCase());
        playgroundReplaceMap.put(GROUP_TAG, playground.getGroup());
        playgroundReplaceMap.put(ROOT_PROJECT_NAME_TAG, playground.isStandalone() ? playground.getName() : "playground");

        //If the playground is new, delete any files create in the event of an error.
        commandQueue.setErrorCallback(new Callback() {
            @Override
            public void onCallback(Exception e) {
                System.err.println("Error building playground. Cause: " + e.getMessage());
                e.printStackTrace();

                if (isStandalone) {
                    deleteDirectory(false, rootDirectory);
                } else {
                    deleteDirectory(false, playground.getRootDirectory());
                }
            }
        });

        if (!playground.getRootDirectory().exists()) {
            commandQueue.add(new CreateDirectoryCommand(playground.getRootDirectory()));
        }

        //TODO Add paths to defaults.toml and just use copy dir method?
        if (!isStandalone)
            commandQueue.add(new CreateDirectoryCommand(new File(playground.getRootDirectory(), "publications")));
        commandQueue.add(new CreateDirectoryCommand(playground.getProjectsDirectory()));
        commandQueue.add(new CopyFileMacroCommand(true, "/setup/playground/gradle.properties", new File(playground.getRootDirectory(), "gradle.properties"), playgroundReplaceMap));
        commandQueue.add(new CopyFileMacroCommand(true, "/setup/playground/buildGradle", new File(playground.getRootDirectory(), "build.gradle"), playgroundReplaceMap));
        commandQueue.add(new CopyFileMacroCommand(true, "/setup/playground/settings.gradle", new File(playground.getRootDirectory(), "settings.gradle"), playgroundReplaceMap));
        commandQueue.add(new WriteToFileCommand(new File(playground.getProjectsDirectory(), ".gitkeep")));

        if (isStandalone) {
            File gradleDirectory = new File(playground.getRootDirectory(), "gradle/wrapper");
            commandQueue.add(new CreateDirectoryCommand(gradleDirectory));
            commandQueue.add(new CreateDirectoryCommand(new File(playground.getRootDirectory(), "assets")));
            commandQueue.add(new CopyFileMacroCommand(true, "/setup/standalone/gradle/wrapper/gradle-wrapper.jar", new File(gradleDirectory, "gradle-wrapper.jar"), null));
            commandQueue.add(new CopyFileMacroCommand(true, "/setup/standalone/gradle/wrapper/gradle-wrapper.properties", new File(gradleDirectory, "gradle-wrapper.properties"), null));
            commandQueue.add(new CopyFileMacroCommand(true, "/setup/standalone/gradlew.bat", new File(rootDirectory, "gradlew.bat"), null));
            commandQueue.add(new CopyFileMacroCommand(true, "/setup/standalone/gradlew", new File(rootDirectory, "gradlew"), null));
            commandQueue.add(new CopyFileMacroCommand(true, "/setup/standalone/gitignore", new File(rootDirectory, "gitignore"), null));
            commandQueue.add(new CopyFileMacroCommand(true, "/setup/standalone/gitattributes", new File(rootDirectory, "gitattributes"), null));
        }
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
            queue.setErrorCallback(new ProjectErrorCallback(logLevel, p));
            mainCommandQueue.add(commandQueue);

            handleLaunchers(d, p);
            projects.add(p);
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
                log(logLevel, LogLevel.ERROR, "Project", builder.toString());
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
                log(logLevel, LogLevel.ERROR, "Project", builder.toString());
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
        map.put(INTEGRATED_DEPENDENCY_TAG, !playground.isStandalone() ? "api \"com.playground.integrated:core:latest.integration\"" : "");
        map.put(USE_GLOBAL_ASSETS_TAG, "true");
        project.setReplaceMap(map);

        if (project.isNewProject()) {
            project.createSourceDirectory(playground);

            StringBuilder builder = LOGGER_BUILDER;
            builder.setLength(0);
            builder.append('(');
            builder.append(project.getName());
            builder.append(") New");
            log(logLevel, LogLevel.INFO, "Project", builder.toString());

            commandQueue
                    .add(new CreateDirectoryCommand(project.getRootDirectory()))
                    .add(new CreateDirectoryCommand(project.getSourceDirectory()))
                    .add(new CreateDirectoryCommand(project.getLaunchersDirectory()))
                    .add(new CreateDirectoryCommand(new File(project.getRootDirectory(), "assets")))
                    .add(new WriteToFileCommand(new File(project.getLaunchersDirectory(), ".gitkeep")));
        } else {
            StringBuilder builder = LOGGER_BUILDER;
            builder.setLength(0);
            builder.append('(');
            builder.append(project.getName());
            builder.append(") Existing");
            log(logLevel, LogLevel.INFO, "Project", builder.toString());
        }

        return project;
    }

    private void handleTemplates(Project project) throws Exception {
        String template = project.getPassedInTemplate();

        TomlTable templateTable = null;

        //If no template is specified the default is used
        if (template == null) {
            template = "libGDX";
            templateTable = settings.getTemplatesTable().getTable(template);
        } else {
            for (String s : settings.getTemplateNames()) {
                if (s.equalsIgnoreCase(template)) {
                    templateTable = settings.getTemplatesTable().getTable(s);
                }
            }
        }

        if (templateTable == null) {
            throw new RuntimeException("Template '" + template + "' not found. Available Templates:" + prettyPrintSet(false, settings.getTemplateNames()));
        }

        boolean isResource = Boolean.TRUE.equals(templateTable.getBoolean("isResource"));
        String baseFilePath = null;

        if (isResource) {
            baseFilePath = "/templates/" + template.toLowerCase();
        } else {
            //For external templates created by user
        }

        queueDirectoryToCopy(isResource, project.getRootDirectory(), templateTable.getTable("root"), baseFilePath + "/root/", project.getReplaceMap(), null);
        queueDirectoryToCopy(isResource, project.getSourceDirectory(), templateTable.getTable("source"), baseFilePath + "/source/", project.getReplaceMap(), new String[]{"Template.java", project.getName() + ".java"});
    }

    private void handleLaunchers(ProjectCommandData data, Project project) throws Exception {
        if (data.getLaunchers() == null) return;

        Map<String, String> replaceMap = new ObjectObjectMap<>(project.getReplaceMap());
        replaceMap.put(GLOBAL_ASSETS_PATH_TAG, playground.isStandalone() ? "assets" : "../assets");

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
                log(logLevel, LogLevel.ERROR, "Project", builder.toString());
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
                log(logLevel, LogLevel.ERROR, "Project", builder.toString());
                continue;
            }

            String group = project.getReplaceMap().get(GROUP_TAG).replace('.', '/') + "/launchers/" + launcherNameLowerCase;
            File sourceDirectory = new File(rootDirectory, "src/main/java/" + group);
            boolean isResource = Boolean.TRUE.equals(launcherTable.getBoolean("isResource"));

            //Create Launcher CommandQueue
            commandQueue = new CommandQueue();

            //In the event of an error delete the launcher directory
            String finalLauncherName = launcherName;
            commandQueue.setErrorCallback(new Callback() {
                @Override
                public void onCallback(Exception e) {
                    deleteDirectory(true, rootDirectory);
                    StringBuilder builder = LOGGER_BUILDER;
                    builder.setLength(0);
                    builder
                            .append('(')
                            .append(project.getName())
                            .append(") Error creating launcher '")
                            .append(finalLauncherName)
                            .append("' - Cause: ")
                            .append(e.getMessage());

                    log(logLevel, LogLevel.ERROR, "Launcher", builder.toString());
                }
            });

            commandQueue.add(new CreateDirectoryCommand(rootDirectory));
            commandQueue.add(new CreateDirectoryCommand(sourceDirectory));

            String baseFilePath = null;

            if (isResource) {
                baseFilePath = "/launchers/" + launcherNameLowerCase;
            } else {
                //For external launchers created by the user
            }

            queueDirectoryToCopy(isResource, rootDirectory, launcherTable.getTable("root"), baseFilePath + "/root/", replaceMap, null);
            queueDirectoryToCopy(isResource, sourceDirectory, launcherTable.getTable("source"), baseFilePath + "/source/", replaceMap, new String[]{"Launcher.java", project.getName() + launcherName + "Launcher.java"});

            project.addLauncher(launcherName);

            mainCommandQueue.add(commandQueue);
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
        File file = new File(playground.getRootDirectory(), "settings.gradle");
        commandQueue.add(new MacroCommand()
                .add(new ReadAsBytesCommand(file))
                .add(new ChangeGradleSettingsCommand(playground, projects))
                .add(new WriteToFileCommand(file)));
    }

}
