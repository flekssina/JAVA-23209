package org.example.factory;

import org.example.commands.Command;
import org.example.exceptions.CommandNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

public class CommandFactory {
    private static final Logger logger = Logger.getLogger(CommandFactory.class.getName());
    private final Map<String, String> commandsMap = new HashMap<>();

    public CommandFactory() {
        loadCommandsFromProperties();
    }

    private void loadCommandsFromProperties() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("command.properties")) {
            if (input == null) {
                logger.severe("Не удалось найти файл commands.properties");
                throw new RuntimeException("Не удалось найти файл commands.properties");
            }

            properties.load(input);

            for (String key : properties.stringPropertyNames()) {
                String commandClass = properties.getProperty(key);
                commandsMap.put(key, commandClass);
                logger.info("Загружено сопоставление команды: " + key + " -> " + commandClass);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка загрузки commands.properties", e);
            throw new RuntimeException("Ошибка загрузки commands.properties", e);
        }
    }

    public Command createCommand(String commandName) throws CommandNotFoundException {
        String commandClassName = commandsMap.get(commandName.toUpperCase());

        if (commandClassName == null) {
            logger.severe("Команда не найдена: " + commandName);
            throw new CommandNotFoundException(commandName);
        }

        try {
            Class<?> commandClass = Class.forName(commandClassName);
            logger.fine("Создание экземпляра команды: " + commandClassName);
            return (Command) commandClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка создания экземпляра команды: " + commandName, e);
            throw new CommandNotFoundException("Ошибка создания команды: " + commandName);
        }
    }
}