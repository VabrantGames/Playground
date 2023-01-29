package com.vabrant.playground.commands;

public interface Command<T, U> {
    T execute(U data) throws Exception;
    default void revert() {}
}
