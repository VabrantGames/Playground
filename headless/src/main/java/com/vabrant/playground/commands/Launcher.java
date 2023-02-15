package com.vabrant.playground.commands;

public class Launcher {

    private boolean hasErrors;
    private String name;
    private String nameLowerCase;

    public Launcher(String name) {
       this.name = name;
       nameLowerCase = name.toLowerCase();
    }

    public void errors() {
        hasErrors = true;
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public String getName() {
        return name;
    }

    public String getNameLowerCase() {
        return nameLowerCase;
    }
}
