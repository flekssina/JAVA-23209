package com.tetris.controller;

import com.tetris.model.GameModel;
import com.tetris.model.HighscoreManager;
import com.tetris.view.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameController {
    private final GameModel model;
    private final GamePanel view;
    private Timer timer;
    private final JFrame parentFrame;
    private final int baseDelay = 500; // Сделано final, так как значение не меняется

    public GameController(GameModel model, GamePanel view, JFrame parentFrame) {
        this.model = model;
        this.view = view;
        this.parentFrame = parentFrame;

        startTimer();
        setupKeyBindings();
    }

    private void startTimer() {
        timer = new Timer(baseDelay, e -> update());
        timer.start();
    }

    private void setupKeyBindings() {
        // Создаем карту привязки клавиш
        InputMap inputMap = view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = view.getActionMap();

        // Движение влево
        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "left");
        actionMap.put("left", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.moveLeft();
                view.repaint();
            }
        });

        // Движение вправо
        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "right");
        actionMap.put("right", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.moveRight();
                view.repaint();
            }
        });

        // Движение вниз
        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "down");
        actionMap.put("down", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.moveDown();
                view.repaint();
            }
        });

        // Поворот
        inputMap.put(KeyStroke.getKeyStroke("UP"), "rotate");
        actionMap.put("rotate", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.rotate();
                view.repaint();
            }
        });

        // Мгновенное падение
        inputMap.put(KeyStroke.getKeyStroke("SPACE"), "drop");
        actionMap.put("drop", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.drop();
                view.repaint();
            }
        });

        // Пауза
        inputMap.put(KeyStroke.getKeyStroke("P"), "pause");
        actionMap.put("pause", new AbstractAction() {
            // Параметр e неиспользуемый, но требуется для реализации интерфейса
            public void actionPerformed(ActionEvent e) {
                togglePause();
            }
        });
    }

    private void togglePause() {
        model.togglePause();
        if (model.isPaused()) {
            timer.stop();
            JOptionPane.showMessageDialog(view, "Игра на паузе", "Пауза", JOptionPane.INFORMATION_MESSAGE);
            timer.start();
            model.togglePause();
        }
        view.repaint();
    }

    private void update() {
        // Обновляем скорость игры в зависимости от уровня
        int newDelay = Math.max(100, baseDelay - (model.getLevel() - 1) * 50);
        if (timer.getDelay() != newDelay) {
            timer.setDelay(newDelay);
        }

        if (!model.isGameOver() && !model.isPaused()) {
            model.moveDown();
            view.repaint();
        } else if (model.isGameOver()) {
            timer.stop();
            showGameOverDialog();
        }
    }

    private void showGameOverDialog() {
        int score = model.getScore();
        HighscoreManager.saveScore(model.getPlayer().getName(), score);

        // Создаем панель с сообщением
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        JLabel messageLabel = new JLabel("Игра окончена! " + model.getPlayer().getName() + " набрал(а) очков: " + score);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(messageLabel, BorderLayout.NORTH);

        // Создаем панель для кнопок
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        // Кнопка "Новая игра"
        JButton newGameButton = new JButton("Новая игра");
        newGameButton.addActionListener(event -> {
            // Закрыть окно
            Window dialog = SwingUtilities.getWindowAncestor(buttonPanel);
            dialog.dispose();

            // Запустить новую игру
            startNewGame();
        });

        // Кнопка "Выход"
        JButton exitButton = new JButton("Выход");
        exitButton.addActionListener(event -> System.exit(0));

        buttonPanel.add(newGameButton);
        buttonPanel.add(exitButton);
        panel.add(buttonPanel, BorderLayout.CENTER);

        // Показываем диалог
        JOptionPane pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        JDialog dialog = pane.createDialog(parentFrame, "Игра окончена");
        dialog.setVisible(true);
    }

    private void startNewGame() {
        // Используем имя текущего игрока вместо создания новой переменной

        // Создаем новую модель и обновляем вид
        GameModel newModel = new GameModel(20, 10, model.getPlayer());
        GamePanel newView = new GamePanel(newModel);

        parentFrame.getContentPane().removeAll();
        parentFrame.add(newView);

        // Создаем новый контроллер
        new GameController(newModel, newView, parentFrame);

        parentFrame.revalidate();
        parentFrame.repaint();
        parentFrame.pack();
        newView.requestFocusInWindow();
    }
}