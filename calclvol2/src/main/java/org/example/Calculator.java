package org.example;

import org.example.context.CalculatorContext;
import org.example.exceptions.CalculatorException;
import org.example.factory.CommandFactory;
import org.example.parser.CommandParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

public class Calculator {
    private static final Logger logger = Logger.getLogger(Calculator.class.getName());
    private final CalculatorContext context;
    private final CommandParser parser;

    public Calculator() {
        this.context = new CalculatorContext();
        CommandFactory factory = new CommandFactory();
        this.parser = new CommandParser(factory);
        logger.info("Калькулятор инициализирован");
    }
    public CommandParser getParser() {
        return parser;
    }

    public CalculatorContext getContext() {
        return context;
    }

    public void processFile(String filename) {
        logger.info("Обработка файла: " + filename);

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                logger.fine("Обработка строки " + lineNumber + ": " + line);

                try {
                    parser.parseLine(line, context);
                } catch (CalculatorException e) {
                    logger.severe("Ошибка в строке " + lineNumber + ": " + e.getMessage());
                    System.err.println("Ошибка в строке " + lineNumber + ": " + e.getMessage());
                }
            }

            logger.info("Обработка файла завершена");
        } catch (IOException e) {
            logger.severe("Ошибка чтения файла: " + e.getMessage());
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
    }
}
