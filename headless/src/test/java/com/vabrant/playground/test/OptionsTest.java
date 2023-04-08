package com.vabrant.playground.test;

import com.github.tommyettinger.ds.ObjectList;
import com.github.tommyettinger.ds.ObjectObjectMap;
import org.junit.jupiter.api.Test;
import org.tomlj.Toml;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class OptionsTest {

    TomlParseResult result;

    private TomlParseResult readTomlFile() throws IOException {
        return Toml.parse(OptionsTest.class.getResourceAsStream("/options/options.toml"));
    }

    @Test
    void ReadOptionsTest() throws Exception {
        TomlParseResult result = readTomlFile();
        TomlTable t = result.getTable("JavaVersion");

        t.entrySet().forEach(e -> {
            System.out.print("Key: " + e.getKey());
            System.out.println(" Value: " + e.getValue());
        });
    }

    @Test
    void MapTest() throws Exception {

        ObjectList<OptionArgument> arguments = new ObjectList<>();
        arguments.add(new OptionArgument("--javaVersion", "11"));
        arguments.add(new OptionArgument("-g", "hello.world"));
        arguments.add(new OptionArgument("-v", "9"));

        //Map alias names to option name
        ObjectObjectMap<String, String> aliasMap = new ObjectObjectMap<>();

        //A lookup to get all options for a scope
        ObjectObjectMap<String, ObjectList<Option>> optionsByScope = new ObjectObjectMap<>();

        //Map option names to option
        ObjectObjectMap<String, Option> options = new ObjectObjectMap<>();

        //A linking of an option scope to a strategy
        ObjectObjectMap<String, Strategy> strategies = new ObjectObjectMap<>();

        TomlParseResult optionsToml = readTomlFile();
        optionsToml.entrySet().forEach(e -> {
            System.out.println("Option: " + e.getKey());
            TomlTable table = optionsToml.getTable(e.getKey());
            Option o = new Option();

            o.name = e.getKey();

            TomlArray alias = table.getArray("alias");
            System.out.println(alias.toList());

            String scope = table.getString("scope");
            if (scope == null) throw new RuntimeException("Scope is null");
            System.out.println("Scope: " + scope);

            o.replaceString = table.getString("replaceString");

            String type = table.getString("type");
            o.type = type;
            System.out.println("InputType: " + type);

            Object def = table.get("default");
            if (def != null) System.out.println("Default: " + def);

            ObjectList<Option> ops = optionsByScope.get(scope);
            if (ops == null) {
                ops = new ObjectList<>();
                optionsByScope.put(scope, ops);
            }
            optionsByScope.get(scope).add(o);

            List<String> list = (List) alias.toList();
            for (String s : list) {
                aliasMap.put(s, o.name);
            }

            options.put(o.name, o);

            if (!strategies.containsValue(o)) {
                switch (scope) {
                    case ".java":
                        strategies.put(scope, new JavaStrategy());
                        break;
                    case "build.gradle":
                        strategies.put(scope, new GradleStrategy());
                        break;
                }
            }

            System.out.println();
        });

        //========== Option Argument Checking ==========//
        for (OptionArgument a : arguments) {
            //Check if an alias exists
            if (!aliasMap.containsKey(a.key)) throw new RuntimeException("Alias doesn't exist");

            Option option = options.get(aliasMap.get(a.key));

            System.out.println("//===== Option Argument Checking =====//");
            System.out.println("Alias: " + a.key + " Option: " + option.getName());

            //Attempt to convert argument
            Object ob = convertArg(option.getType(), a.value);
            a.obj = ob;

            a.option = option;

            //Ensure the value passed in matches the option

            System.out.println();
        }

        File dir = new File(OptionsTest.class.getResource("/options").getFile());

        for (File f : dir.listFiles()) {
            if (f.getName().equals("options.toml")) continue;

            String key = f.getName();
            List<Option> ops = optionsByScope.get(key);
            if (ops == null) {
                if (!key.contains(".")) {
                    System.out.println("No options found");
                    continue;
                }

                key = key.substring(key.indexOf('.'), key.length());
                ops = optionsByScope.get(key);
                if (ops == null) {
                    System.out.println("No options found");
                    continue;
                }
            }

//            strategies.put(options.get("build.gradle").first(), new GradleStrategy());
//            strategies.put(options.get(".java").first(), new JavaStrategy());


            Strategy strat = strategies.get(key);
            strat.execute(arguments, ops, f);

//            for (Option o : ops) {
////                System.out.println("File: " + f.getName() + " Option: " + o.getName());
//            }
        }

    }

    private <T> T convertArg(String type, String arg) {
        switch (type) {
            case "String":
                return (T) arg;
            case "Integer":
                Integer i;
                try {
                    i = Integer.parseInt(arg);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return (T) i;
            default:
                return null;
        }
    }

    private interface Strategy {
        void execute(List<OptionArgument> arguments, List<Option> options, File f);
    }

    private static class GradleStrategy implements Strategy {

        @Override
        public void execute(List<OptionArgument> arguments, List<Option> options, File f) {
            String[] lines;
            StringBuilder builder = new StringBuilder(500);
            try {
                List<String> ln = Files.readAllLines(f.toPath());
                lines = ln.toArray(new String[ln.size()]);

                int idx = arguments.size() - 1;

                for (int i = 0; i < lines.length; i++) {
                    if (arguments.size() == 0) break;

                    String str = null;
                    String l = lines[i];

                    for (int j = arguments.size() - 1; j >= 0; j--) {
                        OptionArgument arg = arguments.get(j);

                        if (l.startsWith(arg.getOption().getReplaceString())) {
                            str = arg.getOption().getReplaceString() + " = " + arg.obj;
                           arguments.remove(j);
                            break;
                        }
                    }

                    if (str == null) {
                        builder.append(l);
                        builder.append(System.lineSeparator());
                    } else {
                        builder.append(str);
                        builder.append(System.lineSeparator());
                    }
                }

                System.out.println("//===== Changed File =====//");
                System.out.println(builder.toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class JavaStrategy implements Strategy {

        @Override
        public void execute(List<OptionArgument> arguments, List<Option> options, File f) {
            System.out.println("Java Strat");
        }
    }

    private static class OptionArgument {
        private String key;
        private String value;
        private Object obj;

        private Option option;

        OptionArgument(String key, String value) {
            this.key = key;
            this.value = value;
        }

        Option getOption() {
            return option;
        }
    }

    private static class Option {

        private String name;
        private String type;
        private String scope;

        private String replaceString;
        private ObjectList<String> rangeList;

        private String getReplaceString() {
            return replaceString;
        }

        private String getName() {
            return name;
        }

        private String getType() {
            return type;
        }

        private String getScope() {
            return scope;
        }

        private List<String> getRange() {
            return rangeList;
        }
    }

}
