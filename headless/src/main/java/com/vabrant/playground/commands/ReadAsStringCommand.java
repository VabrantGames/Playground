package com.vabrant.playground.commands;

import com.playground.commands.Command;
import com.playground.commands.ReadAsBytesCommand;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.function.Function;

@Deprecated
public class ReadAsStringCommand implements Command<String, Object> {

    private final File file;
    private ReadAsBytesCommand rbc;
    private Function<String, String> function;

    public ReadAsStringCommand(File file) {
        this.file = file;
        rbc = new ReadAsBytesCommand(file);
    }

    public ReadAsStringCommand setReadFunction(Function<String, String> function) {

        return this;
    }

    @Override
    public String execute(Object data) throws Exception {
//        return new String(rbc.execute(), StandardCharsets.UTF_8);


        try {
            BufferedReader br = new BufferedReader(new FileReader(file), 1024 * 10);
            StringBuilder builder = new StringBuilder(500);

            String line = null;
            while ((line = br.readLine()) != null) {
                builder.append(function.apply(line));
            }

            br.close();
        } catch (Exception e) {

        }

        return "";
    }
}
