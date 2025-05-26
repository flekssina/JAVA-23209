package com.flekssina.chat.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ServerConfig {
    private static final String CONFIG_FILE = "server.properties";

    private int port;
    private boolean enableLogging;

    public ServerConfig() {
        // Значения по умолчанию
        this.port = 9999;
        this.enableLogging = true;

        loadFromFile();
    }

    private void loadFromFile() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);

            if (properties.containsKey("port")) {
                this.port = Integer.parseInt(properties.getProperty("port"));
            }

            if (properties.containsKey("enableLogging")) {
                this.enableLogging = Boolean.parseBoolean(properties.getProperty("enableLogging"));
            }
        } catch (IOException e) {
            System.out.println("Файл конфигурации не найден. Используются значения по умолчанию.");
        }
    }

    public int getPort() {
        return port;
    }

    public boolean isLoggingEnabled() {
        return enableLogging;
    }
}