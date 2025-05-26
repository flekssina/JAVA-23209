package com.tetris.model;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HighscoreManager {
    private static final String FILE_PATH = "highscores.txt";
    private static final Logger LOGGER = Logger.getLogger(HighscoreManager.class.getName());

    public static void saveScore(String name, int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(name + ":" + score);
            writer.newLine();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to save score", e);
        }
    }

    public static List<String> loadTopScores(int limit) {
        List<String> scores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            reader.lines()
                    .map(line -> line.split(":"))
                    .filter(parts -> parts.length == 2)
                    .map(parts -> new AbstractMap.SimpleEntry<>(parts[0], Integer.parseInt(parts[1])))
                    .sorted((a, b) -> b.getValue() - a.getValue())
                    .limit(limit)
                    .forEach(entry -> scores.add(entry.getKey() + ": " + entry.getValue()));
        } catch (IOException e) {
            // Файл может не существовать — это не ошибка
            LOGGER.log(Level.INFO, "Highscores file not found, this is normal for first run", e);
        }
        return scores;
    }
}