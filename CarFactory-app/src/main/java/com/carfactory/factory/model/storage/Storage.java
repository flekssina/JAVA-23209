package com.carfactory.factory.model.storage;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Storage<T> {
    private final Queue<T> items = new LinkedList<>();
    private final int capacity;
    protected final AtomicInteger produced = new AtomicInteger(0);
    protected final AtomicInteger consumed = new AtomicInteger(0);

    public Storage(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void put(T item) throws InterruptedException {
        while (items.size() >= capacity) {
            wait(); // Ждем, пока не освободится место
        }
        items.add(item);
        produced.incrementAndGet();
        notifyAll(); // Уведомляем потребителей
    }

    public synchronized T get() throws InterruptedException {
        while (items.isEmpty()) {
            wait(); // Ждем, пока не появится хотя бы один элемент
        }
        T item = items.poll();
        consumed.incrementAndGet();
        notifyAll(); // Уведомляем производителей
        return item;
    }

    public synchronized int getSize() {
        return items.size();
    }

    public int getCapacity() {
        return capacity;
    }

    public int getProduced() {
        return produced.get();
    }

    public int getConsumed() {
        return consumed.get();
    }
}