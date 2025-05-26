package com.tetris;

import com.tetris.controller.GameController;
import com.tetris.model.GameModel;
import com.tetris.model.Player;
import com.tetris.view.GamePanel;
import com.tetris.view.MainMenuPanel;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Тетрис");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            showMainMenu(frame);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static void showMainMenu(JFrame frame) {
        MainMenuPanel menuPanel = new MainMenuPanel();
        frame.getContentPane().removeAll();
        frame.add(menuPanel);
        frame.pack();


        menuPanel.setNewGameListener(event -> startNewGame(frame));
        menuPanel.setHighscoresListener(event -> menuPanel.showHighscores());
        menuPanel.setExitListener(event -> System.exit(0));
    }

    private static void startNewGame(JFrame frame) {
        String name = JOptionPane.showInputDialog(frame, "Введи свое имя:", "Имя игрока", JOptionPane.QUESTION_MESSAGE);
        if (name == null) {
            return; // Пользователь отменил ввод имени
        }

        if (name.trim().isEmpty()) {
            name = "Неизвестный";
        }

        Player player = new Player(name);
        GameModel model = new GameModel(20, 10, player);
        GamePanel view = new GamePanel(model);

        frame.getContentPane().removeAll();
        frame.add(view);

        // Создаем и сохраняем ссылку на контроллер
        new GameController(model, view, frame);

        frame.pack();
        view.requestFocusInWindow();
    }
}