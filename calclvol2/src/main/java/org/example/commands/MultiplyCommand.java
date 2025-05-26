package org.example.commands;

import org.example.context.CalculatorContext;
import org.example.exceptions.CommandExecutionException;
import org.example.exceptions.EmptyStackException;

import java.util.logging.Logger;

public class MultiplyCommand implements Command {
    private static final Logger logger = Logger.getLogger(MultiplyCommand.class.getName());

    @Override
    public void execute(CalculatorContext context, String[] args) throws CommandExecutionException {
        if (context.stackSize() < 2) {
            logger.severe("Невозможно выполнить умножение, требуется минимум два элемента в стеке");
            throw new EmptyStackException();
        }

        Double a = context.pop();
        Double b = context.pop();
        Double result = b * a;
        context.push(result);

        logger.info("MULTIPLY: " + b + " * " + a + " = " + result + " (Размер стека: " + context.stackSize() + ")");
    }
}