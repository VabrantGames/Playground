package com.vabrant.playground.commands;

public class CreateReadmeCommand implements Command {

    private WriteToStringCommand wtsc;

    public CreateReadmeCommand(String name) {
        wtsc = new WriteToStringCommand();
        wtsc.append("<h1 align=\"center\">");
        wtsc.append(name);
        wtsc.append("</h1>");
    }

//    public CreateReadmeCommand appendTextLink(String str) {
//        wtsc.append(str);
//        wtsc.newLine();
//        return this;
//    }

    public CreateReadmeCommand appendText(String str) {

        wtsc.append(str);
        return this;
    }

    @Override
    public Object execute(Object data) throws Exception {

        return null;
    }
}
