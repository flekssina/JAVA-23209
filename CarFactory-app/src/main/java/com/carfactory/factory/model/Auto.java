package com.carfactory.factory.model;

public class Auto {
    private static int nextId = 0;
    private final int id;
    private final Body body;
    private final Motor motor;
    private final Accessory accessory;

    public Auto(Body body, Motor motor, Accessory accessory) {
        synchronized(Auto.class) {
            this.id = nextId++;
        }
        this.body = body;
        this.motor = motor;
        this.accessory = accessory;
    }

    public int getId() {
        return id;
    }

    public Body getBody() {
        return body;
    }

    public Motor getMotor() {
        return motor;
    }

    public Accessory getAccessory() {
        return accessory;
    }

    @Override
    public String toString() {
        return "Auto #" + id + " (Body: " + body.getId() + ", Motor: " + motor.getId() + ", Accessory: " + accessory.getId() + ")";
    }
}