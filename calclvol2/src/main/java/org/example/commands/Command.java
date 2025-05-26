package org.example.commands;

import org.example.context.CalculatorContext;
import org.example.exceptions.CommandExecutionException;

public interface Command {
    void execute(CalculatorContext context, String[] args) throws CommandExecutionException;
}