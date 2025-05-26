package org.example.commands;

import org.example.context.CalculatorContext;
import org.example.exceptions.CommandExecutionException;
import org.example.exceptions.EmptyStackException;

import java.util.logging.Logger;

public class SqrtCommand implements Command {
    private static final Logger logger = Logger.getLogger(SqrtCommand.class.getName());

    @Override
    public void execute(CalculatorContext context, String[] args) throws CommandExecutionException {
        if (context.isStackEmpty()) {
            logger.severe("Невозможно выполнить SQRT на пустом стеке");
            throw new EmptyStackException();
        }

        Double a = context.pop();

        if (a < 0) {
            logger.severe("Невозможно вычислить квадратный корень из отрицательного числа: " + a);
            throw new CommandExecutionException("Невозможно вычислить квадратный корень из отрицательного числа: " + a);
        }

        Double result = Math.sqrt(a);
        context.push(result);

        logger.info("SQRT: √" + a + " = " + result + " (Размер стека: " + context.stackSize() + ")");
    }
}