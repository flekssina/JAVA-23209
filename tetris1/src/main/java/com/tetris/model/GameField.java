package com.tetris.model;

import java.awt.Color;

public class GameField {
    private Color[][] grid;
    private final int rows;
    private final int cols;

    public GameField(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Color[rows][cols];
    }

    public Color[][] getGrid() {
        return grid;
    }

    public boolean isOccupied(int y, int x) {
        return y >= 0 && y < rows && x >= 0 && x < cols && grid[y][x] != null;
    }

    public void occupy(int y, int x, Color color) {
        if (y >= 0 && y < rows && x >= 0 && x < cols) {
            grid[y][x] = color;
        }
    }

    public void clearLine(int row) {
        for (int i = row; i > 0; i--) {
            grid[i] = grid[i - 1].clone();
        }
        grid[0] = new Color[cols];
    }

    public boolean isLineFull(int row) {
        for (Color cell : grid[row]) {
            if (cell == null) return false;
        }
        return true;
    }

    public void reset() {
        this.grid = new Color[rows][cols];
    }
}