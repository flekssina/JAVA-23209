package com.flekssina.chat.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ClientConfig {
    private static final String CONFIG_FILE = "client.properties";

    private String serverHost;
    private int serverPort;

    public ClientConfig() {
        // Значения по умолчанию
        this.serverHost = "localhost";
        this.serverPort = 9999;

        loadFromFile();
    }

    private void loadFromFile() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);

            if (properties.containsKey("serverHost")) {
                this.serverHost = properties.getProperty("serverHost");
            }

            if (properties.containsKey("serverPort")) {
                this.serverPort = Integer.parseInt(properties.getProperty("serverPort"));
            }
        } catch (IOException e) {
            System.out.println("Файл конфигурации не найден. Используются значения по умолчанию.");
        }
    }

    public String getServerHost() {
        return serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }
}