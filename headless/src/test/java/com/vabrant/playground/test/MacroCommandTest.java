package com.vabrant.playground.test;

import com.playground.commands.Command;
import com.playground.commands.MacroCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MacroCommandTest {

    @Test
    void Execute() throws Exception {
        MacroCommand command = new MacroCommand();
        command.add(new AddStringCommand("Hello "));
        command.add(new AddStringCommand("World"));
        command.add(new PassThroughCommand());

        String output = (String) command.execute(null);

        assertEquals("Hello World", output);
    }

    static class AddStringCommand implements Command<String, String> {

        String str;

        AddStringCommand(String str) {
            this.str = str;
        }

        @Override
        public String execute(String data) throws Exception {
            if (data != null) str = data + str;
            return str;
        }
    }

    static class PassThroughCommand implements Command<String, String> {

        @Override
        public String execute(String data) throws Exception {
            return data;
        }
    }
}