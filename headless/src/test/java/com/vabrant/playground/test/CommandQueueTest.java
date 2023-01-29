package com.vabrant.playground.test;

import com.vabrant.playground.Callback;
import com.vabrant.playground.commands.Command;
import com.vabrant.playground.commands.CommandQueue;
import org.junit.jupiter.api.Test;

class CommandQueueTest {

    @Test
    void RevertTest() {
        CommandQueue queue = new CommandQueue();
        queue.add(new NumberCommand(1));
        queue.add(new NumberCommand(2));
        queue.add(new NumberCommand(3));
        queue.add(new ErrorCommand());
        queue.add(new NumberCommand(4));

        queue.execute(null);
    }

    @Test
    void NestedTest() {
        CommandQueue queue = new CommandQueue();
        queue.add(new PrintNumberCommand(0));

        CommandQueue nested1 = new CommandQueue();
        nested1.add(new PrintNumberCommand(1));
        nested1.add(new PrintNumberCommand(2));
        nested1.add(new ErrorCommand());
        nested1.add(new PrintNumberCommand(3));
        queue.add(nested1);

        CommandQueue nested2 = new CommandQueue();
        nested2.add(new PrintNumberCommand(11));
        nested2.add(new PrintNumberCommand(22));
        nested2.add(new PrintNumberCommand(33));
        queue.add(nested2);

        queue.execute(null);
    }

    @Test
    void ErrorCallbackTest() {
        CommandQueue queue = new CommandQueue();
        queue.setErrorCallback(new ErrorCallback());
        queue.add(new PrintNumberCommand(1));
        queue.add(new ErrorCommand());

        queue.execute(null);
    }

    static class ErrorCallback implements Callback {

        @Override
        public void onCallback() {
           System.err.println("Error Callback") ;
        }
    }

    static class ErrorCommand implements Command<Object, Object> {

        @Override
        public Object execute(Object data) throws Exception {
            throw new RuntimeException("Some number error");
        }
    }

    static class NumberCommand implements Command<Integer, Object> {

        int num;

        NumberCommand(int num) {
            this.num = num;
        }

        @Override
        public void revert() {
            System.out.println("Revert " + num);
        }

        @Override
        public Integer execute(Object data) throws Exception {
            return null;
        }
    }

    static class PrintNumberCommand extends NumberCommand {

        PrintNumberCommand(int num) {
            super(num);
        }

        @Override
        public Integer execute(Object data) throws Exception {
            System.out.println("Number: " + num);
            return super.execute(data);
        }
    }

}