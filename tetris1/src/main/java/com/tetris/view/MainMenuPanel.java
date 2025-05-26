package com.tetris.view;

import com.tetris.model.HighscoreManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class MainMenuPanel extends JPanel {
    private final JButton newGameButton;
    private final JButton highscoresButton;
    private final JButton exitButton;

    public MainMenuPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 600));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(20, 20, 40));
        JLabel titleLabel = new JLabel("ТЕТРИС");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 0, 20));
        buttonPanel.setBackground(new Color(20, 20, 40));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));

        newGameButton = createButton("Новая игра");
        highscoresButton = createButton("Таблица очков");
        exitButton = createButton("Выход");

        buttonPanel.add(newGameButton);
        buttonPanel.add(highscoresButton);
        buttonPanel.add(exitButton);

        add(titlePanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        setBackground(new Color(20, 20, 40));
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBackground(new Color(60, 60, 100));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return button;
    }

    public void setNewGameListener(ActionListener listener) {
        newGameButton.addActionListener(listener);
    }

    public void setHighscoresListener(ActionListener listener) {
        highscoresButton.addActionListener(listener);
    }

    public void setExitListener(ActionListener listener) {
        exitButton.addActionListener(listener);
    }

    public void showHighscores() {
        List<String> scores = HighscoreManager.loadTopScores(10);
        StringBuilder message = new StringBuilder("ТОП ОЧКОВ:\n\n");

        if (scores.isEmpty()) {
            message.append("НЕТ ОЧКОВ");
        } else {
            for (int i = 0; i < scores.size(); i++) {
                message.append(i + 1).append(". ").append(scores.get(i)).append("\n");
            }
        }

        JOptionPane.showMessageDialog(this, message.toString(), "ТОП ОЧКОВ", JOptionPane.INFORMATION_MESSAGE);
    }
}