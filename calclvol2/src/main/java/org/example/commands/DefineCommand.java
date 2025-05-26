package org.example.commands;

import org.example.context.CalculatorContext;
import org.example.exceptions.CommandExecutionException;

import java.util.logging.Logger;

public class DefineCommand implements Command {
    private static final Logger logger = Logger.getLogger(DefineCommand.class.getName());

    @Override
    public void execute(CalculatorContext context, String[] args) throws CommandExecutionException {
        if (args.length != 2) {
            throw new CommandExecutionException("Команда DEFINE требует два аргумента: имя и значение");
        }

        String name = args[0];

        try {
            // Пробуем разобрать второй аргумент как число
            Double value = Double.parseDouble(args[1]);
            context.defineVariable(name, value);
            logger.info("DEFINE: " + name + " = " + value);
        } catch (NumberFormatException e) {
            // Проверяем, является ли второй аргумент определенной переменной
            if (context.getVariables().containsKey(args[1])) {
                Double value = context.getVariable(args[1]);
                context.defineVariable(name, value);
                logger.info("DEFINE: " + name + " = " + value + " (из переменной " + args[1] + ")");
            } else {
                logger.severe("Не удалось разобрать значение DEFINE: " + args[1]);
                throw new CommandExecutionException("Неверный формат числа в команде DEFINE: " + args[1], e);
            }
        }
    }
}