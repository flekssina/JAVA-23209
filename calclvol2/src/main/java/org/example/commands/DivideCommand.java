package org.example.commands;

import org.example.context.CalculatorContext;
import org.example.exceptions.CommandExecutionException;
import org.example.exceptions.EmptyStackException;

import java.util.logging.Logger;

public class DivideCommand implements Command {
    private static final Logger logger = Logger.getLogger(DivideCommand.class.getName());

    @Override
    public void execute(CalculatorContext context, String[] args) throws CommandExecutionException {
        if (context.stackSize() < 2) {
            logger.severe("Невозможно выполнить деление, требуется минимум два элемента в стеке");
            throw new EmptyStackException();
        }

        Double a = context.pop();
        Double b = context.pop();

        if (a == 0) {
            logger.severe("Деление на ноль");
            throw new CommandExecutionException("Деление на ноль");
        }

        Double result = b / a;
        context.push(result);

        logger.info("DIVIDE: " + b + " / " + a + " = " + result + " (Размер стека: " + context.stackSize() + ")");
    }
}