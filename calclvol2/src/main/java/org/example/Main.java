package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        logger.setLevel(Level.OFF);

        try {

            LogManager.getLogManager().readConfiguration(
                    Main.class.getClassLoader().getResourceAsStream("logging.properties")
            );
            logger.info("Настройки логирования загружены");
        } catch (IOException | NullPointerException e) {
            System.err.println("Ошибка при загрузке настроек логирования: " + e.getMessage());
        }

        logger.info("Приложение запущено");

        Calculator calculator = new Calculator();

        if (args.length == 1) {
            // Режим работы с файлом
            String filename = args[0];
            calculator.processFile(filename);
        } else {
            // Интерактивный режим
            logger.info("Запуск в интерактивном режиме");
            System.out.println("Интерактивный режим. Введите команды (введите 'exit' для выхода):");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                String line;
                while (true) {
                    System.out.print("> ");
                    line = reader.readLine();
                    if (line == null || line.equalsIgnoreCase("exit")) {
                        System.out.println("Выход из программы.");
                        break;
                    }

                    try {
                        calculator.getParser().parseLine(line, calculator.getContext());
                    } catch (Exception e) {
                        System.err.println("Ошибка: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                logger.severe("Ошибка чтения из терминала: " + e.getMessage());
            }
        }

        logger.info("Приложение завершено");
    }
}