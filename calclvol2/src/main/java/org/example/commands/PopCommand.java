package org.example.commands;

import org.example.context.CalculatorContext;
import org.example.exceptions.CommandExecutionException;
import org.example.exceptions.EmptyStackException;

import java.util.logging.Logger;

public class PopCommand implements Command {
    private static final Logger logger = Logger.getLogger(PopCommand.class.getName());

    @Override
    public void execute(CalculatorContext context, String[] args) throws CommandExecutionException {
        if (context.isStackEmpty()) {
            logger.severe("Невозможно выполнить POP на пустом стеке");
            throw new EmptyStackException();
        }

        Double value = context.pop();
        logger.info("POP: " + value + " (Размер стека: " + context.stackSize() + ")");
    }
}