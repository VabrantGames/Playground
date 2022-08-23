package com.vabrant.playground.builders.libgdx;

import com.vabrant.playground.Builder;
import com.vabrant.playground.Playground;
import com.vabrant.playground.commands.CreateDirectoryCommand;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LibgdxBuilder implements Builder {

    @Override
    public void init(Playground playground) {
        Path rootLaunchersPath = Paths.get(playground.getPathStr(Playground.ROOT_PATH) +
                playground.getPathStr(Playground.LAUNCHERS_PATH_RELATIVE) + "/desktop");

        //Create root desktop directory if it does not exist
        if (!Files.exists(rootLaunchersPath)) {
            playground.addCommand(new CreateDirectoryCommand(rootLaunchersPath));
        }



//        playground.addCommand(
//                new CreateDirectoryCommand(
//                        playground.getPathStr(Playground.ROOT_PATH) +
//                                playground.getPathStr(Playground.LAUNCHERS_PATH_RELATIVE) + "/desktop"));
    }
}
