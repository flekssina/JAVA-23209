package com.flekssina.chat.client;

import com.flekssina.chat.client.ui.LoginFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class ClientApp {
    private static final Logger logger = LoggerFactory.getLogger(ClientApp.class);

    public static void main(String[] args) {
        try {
            // Установка look and feel системы
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        } catch (Exception e) {
            logger.error("Ошибка при запуске клиента", e);
        }
    }
}