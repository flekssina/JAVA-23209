package com.tetris.model;

import java.awt.Color;

public class GameModel {
    private final GameField field;
    private final Player player;
    private Tetromino current;
    private Tetromino next;
    private int x, y;
    private int score = 0;
    private int level = 1;
    private int linesCleared = 0;
    private boolean gameOver = false;
    private boolean paused = false;

    public GameModel(int rows, int cols, Player player) {
        this.field = new GameField(rows, cols);
        this.player = player;
        this.next = Tetromino.getRandomTetromino();
        spawnNew();
    }

    public void spawnNew() {
        current = next;
        next = Tetromino.getRandomTetromino();
        x = field.getGrid()[0].length / 2 - current.getShape()[0].length / 2;
        y = 0;
        if (collides(x, y)) {
            gameOver = true;
        }
    }

    public void moveDown() {
        if (paused || gameOver) return;

        if (!collides(x, y + 1)) {
            y++;
        } else {
            merge();
            int linesRemoved = clearLines();
            updateScore(linesRemoved);
            spawnNew();
        }
    }

    public void moveLeft() {
        if (paused || gameOver) return;
        if (!collides(x - 1, y)) x--;
    }

    public void moveRight() {
        if (paused || gameOver) return;
        if (!collides(x + 1, y)) x++;
    }

    public void rotate() {
        if (paused || gameOver) return;
        Tetromino rotated = current.rotate();

        // Проверяем возможность поворота с текущей позиции
        if (!collides(x, y, rotated)) {
            current = rotated;
            return;
        }

        // Если не получается, пробуем сместить фигуру влево/вправо (wall kick)
        int[] offsets = { -1, 1, -2, 2 };
        for (int offset : offsets) {
            if (!collides(x + offset, y, rotated)) {
                current = rotated;
                x += offset;
                return;
            }
        }
    }

    public void drop() {
        if (paused || gameOver) return;
        int dropY = getShadowY();
        if (dropY > y) {
            y = dropY;
            merge();
            int linesRemoved = clearLines();
            updateScore(linesRemoved);
            spawnNew();
        }
    }

    public int getShadowY() {
        int shadowY = y;
        while (!collides(x, shadowY + 1)) {
            shadowY++;
        }
        return shadowY;
    }

    private void merge() {
        int[][] shape = current.getShape();
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {
                if (shape[r][c] == 1) {
                    field.occupy(y + r, x + c, current.getColor());
                }
            }
        }
    }

    private int clearLines() {
        Color[][] grid = field.getGrid();
        int linesRemoved = 0;

        for (int row = 0; row < grid.length; row++) {
            boolean isFull = true;
            for (int col = 0; col < grid[0].length; col++) {
                if (grid[row][col] == null) {
                    isFull = false;
                    break;
                }
            }

            if (isFull) {
                field.clearLine(row);
                linesRemoved++;
            }
        }

        linesCleared += linesRemoved;
        return linesRemoved;
    }

    private void updateScore(int linesRemoved) {
        if (linesRemoved == 0) return;

        // Базовые очки за линии: 100, 300, 500, 800
        int[] baseScores = { 0, 100, 300, 500, 800 };

        // Расчет очков с учетом уровня
        score += baseScores[linesRemoved] * level;

        // Повышение уровня после каждых 10 линий
        level = (linesCleared / 10) + 1;
    }

    public boolean collides(int newX, int newY) {
        return collides(newX, newY, current);
    }

    public boolean collides(int newX, int newY, Tetromino tetromino) {
        int[][] shape = tetromino.getShape();
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {
                if (shape[r][c] == 1) {
                    int boardX = newX + c;
                    int boardY = newY + r;

                    // Проверка границ поля
                    if (boardX < 0 || boardX >= field.getGrid()[0].length || boardY >= field.getGrid().length) {
                        return true;
                    }

                    // Проверка коллизии с другими блоками (если не за верхней границей)
                    if (boardY >= 0 && field.getGrid()[boardY][boardX] != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Геттеры
    public int getCurrentX() { return x; }
    public int getCurrentY() { return y; }
    public int[][] getCurrentTetrominoMatrix() { return current.getShape(); }
    public Color getCurrentColor() { return current.getColor(); }
    public int[][] getNextTetrominoMatrix() { return next.getShape(); }
    public Color getNextColor() { return next.getColor(); }
    public Color[][] getFieldGrid() { return field.getGrid(); }
    public int getScore() { return score; }
    public int getLevel() { return level; }
    public int getLinesCleared() { return linesCleared; }
    public boolean isGameOver() { return gameOver; }
    public boolean isPaused() { return paused; }
    public Player getPlayer() { return player; }

    public void togglePause() {
        paused = !paused;
    }
}