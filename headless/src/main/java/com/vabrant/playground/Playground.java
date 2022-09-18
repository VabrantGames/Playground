package com.vabrant.playground;

import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

import static com.vabrant.playground.Setup.DIRECTORIES;

@CommandLine.Command(
        name = "playground",
        description = "Initial a playground.",
        mixinStandardHelpOptions = true
)
public class Playground implements Callable<Integer> {

    @CommandLine.Mixin
    private Setup.ShareableOptions options;

    @Override
    public Integer call() throws Exception {
        final String playgroundDirectory = "playground";

        File f = new File(options.playgroundPath);


//        DIRECTORIES.put(playgroundDirectory, new File(options.))

        return 0;
    }
}
