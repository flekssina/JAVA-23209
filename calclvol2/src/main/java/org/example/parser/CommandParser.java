package org.example.parser;

import org.example.commands.Command;
import org.example.context.CalculatorContext;
import org.example.exceptions.CalculatorException;
import org.example.exceptions.CommandParseException;
import org.example.factory.CommandFactory;

import java.util.logging.Logger;
import java.util.logging.Level;

public class CommandParser {
    private static final Logger logger = Logger.getLogger(CommandParser.class.getName());
    private final CommandFactory factory;

    public CommandParser(CommandFactory factory) {
        this.factory = factory;
    }

    public void parseLine(String line, CalculatorContext context) throws CalculatorException {
        // Пропускаем пустые строки
        if (line == null || line.trim().isEmpty()) {
            return;
        }

        // Пропускаем комментарии
        if (line.trim().startsWith("#")) {
            logger.fine("Пропуск комментария: " + line);
            return;
        }

        String[] parts = line.trim().split("\\s+");
        String commandName = parts[0].toUpperCase();

        // Если команда - одна из арифметических операций
        if (commandName.equals("+")) {
            commandName = "ADD";
        } else if (commandName.equals("-")) {
            commandName = "SUBTRACT";
        } else if (commandName.equals("*")) {
            commandName = "MULTIPLY";
        } else if (commandName.equals("/")) {
            commandName = "DIVIDE";
        }

        logger.fine("Разбор команды: " + commandName);

        // Создаем массив аргументов без имени команды
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);

        try {
            Command command = factory.createCommand(commandName);
            command.execute(context, args);
        } catch (CalculatorException e) {
            logger.log(Level.SEVERE, "Ошибка выполнения команды " + commandName + ": " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Непредвиденная ошибка при обработке команды " + commandName + ": " + e.getMessage(), e);
            throw new CommandParseException("Непредвиденная ошибка при обработке команды: " + commandName, e);
        }
    }
}