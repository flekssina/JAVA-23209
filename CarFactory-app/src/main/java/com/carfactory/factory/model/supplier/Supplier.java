package com.carfactory.factory.model.supplier;

import com.carfactory.config.FactoryConfig;
import com.carfactory.factory.model.Detail;
import com.carfactory.factory.model.storage.Storage;

public abstract class Supplier<T extends Detail> extends Thread {
    protected final Storage<T> storage;
    protected volatile boolean running = true;
    protected volatile int delay;

    public Supplier(Storage<T> storage, int delay) {
        this.storage = storage;
        this.delay = delay;
    }

    protected abstract T createDetail();

    @Override
    public void run() {
        while (running) {
            try {
                T detail = createDetail();
                storage.put(detail);
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void stopSupplier() {
        running = false;
        interrupt();
    }
}