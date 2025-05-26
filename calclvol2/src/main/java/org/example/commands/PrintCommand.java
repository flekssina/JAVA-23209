package org.example.commands;

import org.example.context.CalculatorContext;
import org.example.exceptions.CommandExecutionException;
import org.example.exceptions.EmptyStackException;

import java.util.logging.Logger;

public class PrintCommand implements Command {
    private static final Logger logger = Logger.getLogger(PrintCommand.class.getName());

    @Override
    public void execute(CalculatorContext context, String[] args) throws CommandExecutionException {
        if (context.isStackEmpty()) {
            logger.severe("Невозможно выполнить PRINT на пустом стеке");
            throw new EmptyStackException();
        }

        Double value = context.peek();
        System.out.println(value);
        logger.info("PRINT: " + value + " (Размер стека: " + context.stackSize() + ")");
    }
}