package com.vabrant.playground;

import com.moandjiezana.toml.Toml;
import picocli.CommandLine;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

@CommandLine.Command(
        subcommands = {Init.class},
        name = "Playground",
        version = "0.0.1",
        description = "Create a playground for playing around with things libgdx related.",
        mixinStandardHelpOptions = true
)
public class Setup {

    public static void init(String path) {

       File f = new File(path);

       if (!f.isDirectory()) throw new RuntimeException("Not a directory");

//       if (Utils.isIntegrated(f)) {
//           f = new File(f, "playgrounds/settings.toml");
//       }
//       else {
//          f = new File(f, "settings.toml") ;
//       }

       if (!f.exists()) throw new RuntimeException("No settings.toml found in " + path);

       Toml toml = new Toml().read(f);



    }

    public static final String PACKAGE = "${PACKAGE}";
    public static final String PLAYGROUND_PROJECT_NAME = "${PROJECT_PROJECT_NAME}";
    public static final String ROOT_ASSETS = "${ROOT_ASSETS}";
    public static final String PLAYGROUND_NAME =  "${PLAYGROUND_NAME}";

    public static final String NAME = "name";

    public static final String PLAYGROUND_PATH = "playgroundPath";

    public static void main(String[] args) {
        String[] init = {"init", "-h"};
        String[] def = {"-h"};
        String[] init2 = {"init", "-n=MyPlayground", "-p=D:/developer/java/PlaygroundOutputTest/Normal"};
        String[] init3 = {"init", "-n=MyPlayground", "-p=D:/developer/java/PlaygroundOutputTest/Integrated"};
        String[] playground1 = {"setup/playground", "-n=ShapeDrawer", "-p=D:/developer/java/PlaygroundOutputTest/Normal"};

        int exitCode = new CommandLine(new Setup()).execute(playground1);
        System.exit(exitCode);
    }

    public static final String ROOT_PATH = "rootpath";

    static class ShareableOptions {
        @CommandLine.Option(
                names = {"-n", "--name"},
                description = "",
                required = true
        )
        String name;

        @CommandLine.Option(
                names = {"-p", "--path"},
                description = "Path to playground directory.",
                paramLabel = "path",
                required = true
        )
        String playgroundPath;
    }

    public static final String LAUNCHERS_PATH_RELATIVE = "launchers";

    public static CommandQueue commandStack = new CommandQueue();
    public final Map<String, Path> paths = new HashMap<>();
    public static final Map<String, File> DIRECTORIES = new HashMap<>();

    public Path getPath(String name) {
        return paths.get(name);
    }

    public String getPathStr(String name) {
        return paths.get(name).toString();
    }

    public static File getDirectoryFile(String key) {
        return Objects.requireNonNull(DIRECTORIES.get(key), "No such directory file with alias: " + key);
    }

    public static void setDirectoryFile(String key, File file) {

    }

    public static Object checkNull(Object o, String message) {
//        if (o == null) throw new RuntimeException("Object is null");
        Objects.requireNonNull(o, message);
        return o;
    }

    public static File newFile(File f, String child) {
        Objects.requireNonNull(f, "Path is null");
        return new File(f, child);
    }

    public static boolean isEmpty(File f) {
        String[] l = f.list();
        return l == null || l.length == 0;
    }

    static Path getResource(String resource) throws URISyntaxException {
        return Paths.get(Setup.class.getResource(resource).toURI());
    }

    static void copyFile(Path from, Path to) throws Exception {
        Files.copy(from, to);
    }

    static void copyDirectory(File from, File to) throws Exception {
        to.mkdirs();

        File[] files = from.listFiles();
        if (files != null) {
            for (File src : files) {
                File dest = new File(to, src.getName());
                if (src.isDirectory()) {
                    copyDirectory(src, dest);
                } else {
                    copyFile(src.toPath(), dest.toPath());
                }
            }
        }
    }

    public static Map<String, String> createMap(Consumer<Map<String, String>> c) {
        Map<String, String> map = new HashMap<>();
        c.accept(map);
        return map;
    }

    static String makeFirstLetterLowerCase(String s) {
        if (Character.isLowerCase(s.charAt(0))) return s;
        char[] c = s.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return new String(c);
    }

//    public static byte[] read(File file) throws Exception {
//        try (InputStream is = new FileInputStream(file); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//            byte[] buffer = new byte[1024 * 5];
//            int read = 0;
//            while ((read = is.read(buffer)) != -1) {
//                baos.write(buffer, 0, read);
//            }
//            return baos.toByteArray();
//        }
//    }
//
//    public static String readAsString(File file) throws Exception {
//        return new String(read(file), StandardCharsets.UTF_8);
//    }
//
//    public static void write(File file, byte[] bytes) throws Exception {
//        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
//           os.write(bytes);
//           os.flush();
//        }
//    }
//
//    public static void copy(File srcFile, File destFile) throws Exception {
//        write(destFile, read(srcFile));
//    }

    public static void execute(File file, String... commands) throws Exception {
//        Process p = new ProcessBuilder(commands)
    }

}
