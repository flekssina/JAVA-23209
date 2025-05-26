package com.tetris.model;

import java.awt.Color;

public class Tetromino {
    private final int[][] shape;
    private final Color color;

    public Tetromino(int[][] shape, Color color) {
        this.shape = shape;
        this.color = color;
    }

    public int[][] getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    public static Tetromino getRandomTetromino() {
        Color[] colors = { Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.ORANGE, Color.GREEN, Color.BLUE, Color.RED };
        int[][][] shapes = {
                {{1, 1, 1, 1}},                 // I
                {{1, 1}, {1, 1}},              // O
                {{0, 1, 0}, {1, 1, 1}},        // T
                {{0, 1, 1}, {1, 1, 0}},        // S
                {{1, 1, 0}, {0, 1, 1}},        // Z
                {{1, 0, 0}, {1, 1, 1}},        // J
                {{0, 0, 1}, {1, 1, 1}}         // L
        };
        int i = (int)(Math.random() * shapes.length);
        return new Tetromino(shapes[i], colors[i]);
    }

    public Tetromino rotate() {
        int rows = shape.length;
        int cols = shape[0].length;
        int[][] rotated = new int[cols][rows];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                rotated[c][rows - 1 - r] = shape[r][c];
            }
        }
        return new Tetromino(rotated, color);
    }
}