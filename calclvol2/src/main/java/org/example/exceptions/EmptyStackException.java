package org.example.exceptions;

public class EmptyStackException extends CommandExecutionException {
    public EmptyStackException() {
        super("Operation cannot be performed on an empty stack");
    }
}