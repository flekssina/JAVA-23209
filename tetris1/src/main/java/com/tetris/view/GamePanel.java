package com.tetris.view;

import com.tetris.model.GameModel;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private final GameModel model;
    private static final int BLOCK_SIZE = 30;

    public GamePanel(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(500, 600));
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Отрисовка границы игрового поля
        Color[][] grid = model.getFieldGrid();
        int fieldWidth = grid[0].length * BLOCK_SIZE;
        int fieldHeight = grid.length * BLOCK_SIZE;

        g2d.setColor(new Color(40, 40, 60));
        g2d.fillRect(0, 0, fieldWidth, fieldHeight);

        // Отрисовка сетки
        g2d.setColor(new Color(70, 70, 90));
        for (int x = 0; x <= fieldWidth; x += BLOCK_SIZE) {
            g2d.drawLine(x, 0, x, fieldHeight);
        }
        for (int y = 0; y <= fieldHeight; y += BLOCK_SIZE) {
            g2d.drawLine(0, y, fieldWidth, y);
        }

        // Отрисовка поля
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                if (grid[y][x] != null) {
                    drawBlock(g2d, x, y, grid[y][x]);
                }
            }
        }

        // Отрисовка тени текущей фигуры
        int shadowY = model.getShadowY();
        int[][] shape = model.getCurrentTetrominoMatrix();
        int offsetX = model.getCurrentX();
        int offsetY = shadowY;

        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {
                if (shape[r][c] == 1) {
                    g2d.setColor(new Color(50, 50, 50, 120));
                    g2d.fillRect((offsetX + c) * BLOCK_SIZE, (offsetY + r) * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                    g2d.setColor(new Color(100, 100, 100, 120));
                    g2d.drawRect((offsetX + c) * BLOCK_SIZE, (offsetY + r) * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }

        // Отрисовка текущей фигуры
        Color color = model.getCurrentColor();
        offsetX = model.getCurrentX();
        offsetY = model.getCurrentY();

        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {
                if (shape[r][c] == 1) {
                    drawBlock(g2d, offsetX + c, offsetY + r, color);
                }
            }
        }

        // Боковая панель
        int sidebarX = fieldWidth + 10;
        int sidebarWidth = 160;

        // Фон для боковой панели
        g2d.setColor(new Color(30, 30, 50));
        g2d.fillRect(sidebarX, 0, sidebarWidth, fieldHeight);

        // Отображение следующей фигуры
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("СЛЕДУЮЩИЙ:", sidebarX + 10, 30);

        int[][] nextShape = model.getNextTetrominoMatrix();
        Color nextColor = model.getNextColor();

        // Центрирование следующей фигуры
        int nextBlockSize = 20;
        int nextShapeWidth = nextShape[0].length * nextBlockSize;
        int nextShapeX = sidebarX + (sidebarWidth - nextShapeWidth) / 2;
        int nextShapeY = 50;

        for (int r = 0; r < nextShape.length; r++) {
            for (int c = 0; c < nextShape[0].length; c++) {
                if (nextShape[r][c] == 1) {
                    g2d.setColor(nextColor);
                    g2d.fillRect(nextShapeX + c * nextBlockSize, nextShapeY + r * nextBlockSize,
                            nextBlockSize, nextBlockSize);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(nextShapeX + c * nextBlockSize, nextShapeY + r * nextBlockSize,
                            nextBlockSize, nextBlockSize);
                }
            }
        }

        // Отображение счёта и имени
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("ИГРОК:", sidebarX + 10, 150);
        g2d.drawString(model.getPlayer().getName(), sidebarX + 10, 175);

        g2d.drawString("ОЧКИ:", sidebarX + 10, 220);
        g2d.drawString(String.valueOf(model.getScore()), sidebarX + 10, 245);

        g2d.drawString("ЛИНИИ:", sidebarX + 10, 290); // Перемещено вверх с 360
        g2d.drawString(String.valueOf(model.getLinesCleared()), sidebarX + 10, 315); // Перемещено вверх с 385

        // Управление
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("УПРАВЛЕНИЕ:", sidebarX + 10, 370); // Перемещено вверх с 450
        g2d.drawString("← → : Движение", sidebarX + 10, 390); // Перемещено вверх
        g2d.drawString("↑ : Поворот", sidebarX + 10, 410); // Перемещено вверх
        g2d.drawString("↓ : Мягкое падение", sidebarX + 10, 430); // Перемещено вверх
        g2d.drawString("Space : Жесткое падение", sidebarX + 10, 450); // Перемещено вверх
        g2d.drawString("P : Пауза", sidebarX + 10, 470); // Перемещено вверх
    }

    private void drawBlock(Graphics2D g, int x, int y, Color color) {
        // Основной цвет блока
        g.setColor(color);
        g.fillRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);

        // Светлая грань (3D эффект)
        g.setColor(color.brighter());
        g.drawLine(x * BLOCK_SIZE, y * BLOCK_SIZE,
                x * BLOCK_SIZE + BLOCK_SIZE - 1, y * BLOCK_SIZE);
        g.drawLine(x * BLOCK_SIZE, y * BLOCK_SIZE,
                x * BLOCK_SIZE, y * BLOCK_SIZE + BLOCK_SIZE - 1);

        // Темная грань (3D эффект)
        g.setColor(color.darker());
        g.drawLine(x * BLOCK_SIZE + BLOCK_SIZE - 1, y * BLOCK_SIZE,
                x * BLOCK_SIZE + BLOCK_SIZE - 1, y * BLOCK_SIZE + BLOCK_SIZE - 1);
        g.drawLine(x * BLOCK_SIZE, y * BLOCK_SIZE + BLOCK_SIZE - 1,
                x * BLOCK_SIZE + BLOCK_SIZE - 1, y * BLOCK_SIZE + BLOCK_SIZE - 1);

        // Внутренняя сетка блока
        g.setColor(new Color(0, 0, 0, 50));
        g.drawRect(x * BLOCK_SIZE + 2, y * BLOCK_SIZE + 2, BLOCK_SIZE - 4, BLOCK_SIZE - 4);
    }
}