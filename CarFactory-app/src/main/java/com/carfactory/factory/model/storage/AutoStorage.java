package com.carfactory.factory.model.storage;

import com.carfactory.factory.model.Auto;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoStorage extends Storage<Auto> {
    private final AtomicInteger sold = new AtomicInteger(0);

    public AutoStorage(int capacity) {
        super(capacity);
    }

    @Override
    public synchronized Auto get() throws InterruptedException {
        Auto auto = super.get();
        sold.incrementAndGet();
        notifyAll(); // Уведомляем контроллер
        return auto;
    }

    public int getSold() {
        return sold.get();
    }
}