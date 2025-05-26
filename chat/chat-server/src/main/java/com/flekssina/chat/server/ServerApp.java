package com.flekssina.chat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerApp {
    private static final Logger logger = LoggerFactory.getLogger(ServerApp.class);

    public static void main(String[] args) {
        try {
            ServerConfig config = new ServerConfig();
            ChatServer server = new ChatServer(config);

            server.start();

            // Добавление обработчика для корректного завершения сервера
            Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

            logger.info("Для остановки сервера нажмите Ctrl+C");
        } catch (Exception e) {
            logger.error("Ошибка при запуске сервера", e);
        }
    }
}