package org.example.commands;

import org.example.context.CalculatorContext;
import org.example.exceptions.CommandExecutionException;
import org.example.exceptions.EmptyStackException;

import java.util.logging.Logger;

public class AddCommand implements Command {
    private static final Logger logger = Logger.getLogger(AddCommand.class.getName());

    @Override
    public void execute(CalculatorContext context, String[] args) throws CommandExecutionException {
        if (context.stackSize() < 2) {
            logger.severe("Невозможно выполнить сложение, требуется минимум два элемента в стеке");
            throw new EmptyStackException();
        }

        Double a = context.pop();
        Double b = context.pop();
        Double result = b + a;
        context.push(result);

        logger.info("ADD: " + b + " + " + a + " = " + result + " (Размер стека: " + context.stackSize() + ")");
    }
}