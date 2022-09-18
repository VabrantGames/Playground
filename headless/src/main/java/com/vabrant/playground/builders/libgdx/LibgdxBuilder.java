package com.vabrant.playground.builders.libgdx;

import com.vabrant.playground.Builder;
import com.vabrant.playground.Setup;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LibgdxBuilder implements Builder {

    @Override
    public void init(Setup setup) {
        Path rootLaunchersPath = Paths.get(setup.getPathStr(Setup.ROOT_PATH) +
                setup.getPathStr(Setup.LAUNCHERS_PATH_RELATIVE) + "/desktop");

        //Create root desktop directory if it does not exist
        if (!Files.exists(rootLaunchersPath)) {
//            playground.addCommand(new CreateDirectoryCommand(rootLaunchersPath));
        }



//        playground.addCommand(
//                new CreateDirectoryCommand(
//                        playground.getPathStr(Playground.ROOT_PATH) +
//                                playground.getPathStr(Playground.LAUNCHERS_PATH_RELATIVE) + "/desktop"));
    }
}
