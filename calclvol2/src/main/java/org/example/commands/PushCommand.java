package org.example.commands;

import org.example.context.CalculatorContext;
import org.example.exceptions.CommandExecutionException;

import java.util.logging.Logger;

public class PushCommand implements Command {
    private static final Logger logger = Logger.getLogger(PushCommand.class.getName());

    @Override
    public void execute(CalculatorContext context, String[] args) throws CommandExecutionException {
        if (args.length != 1) {
            throw new CommandExecutionException("Команда PUSH требует ровно один аргумент");
        }

        try {
            String arg = args[0];
            Double value;

            // Проверяем, является ли аргумент переменной
            if (context.getVariables().containsKey(arg)) {
                value = context.getVariable(arg);
                logger.fine("Использование переменной " + arg + " со значением " + value);
            } else {
                // Пробуем разобрать как число
                value = Double.parseDouble(arg);
            }

            context.push(value);
            logger.info("PUSH: " + value + " (Размер стека: " + context.stackSize() + ")");
        } catch (NumberFormatException e) {
            logger.severe("Не удалось разобрать аргумент PUSH: " + args[0]);
            throw new CommandExecutionException("Неверный формат числа в команде PUSH: " + args[0], e);
        }
    }
}