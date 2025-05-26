package com.carfactory;

import com.carfactory.factory.controller.FactoryController;
import com.carfactory.factory.view.FactoryView;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        try {
            // Установка стиля Look and Feel Nimbus для более красивого интерфейса
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // В случае ошибки используем стандартный стиль
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        SwingUtilities.invokeLater(() -> {
            FactoryController controller = new FactoryController();
            FactoryView view = new FactoryView(controller);
            view.setVisible(true);
            controller.startFactory();
        });
    }
}