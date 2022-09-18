package com.vabrant.playground.commands;

public interface Command<T, U> {
    T execute() throws Exception;
    default void setData(U data) {}

}
