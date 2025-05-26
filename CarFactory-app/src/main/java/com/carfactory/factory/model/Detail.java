package com.carfactory.factory.model;

public abstract class Detail {
    private static int nextId = 0;
    private final int id;

    public Detail() {
        synchronized(Detail.class) {
            this.id = nextId++;
        }
    }

    public int getId() {
        return id;
    }
}