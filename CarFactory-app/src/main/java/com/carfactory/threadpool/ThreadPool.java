package com.carfactory.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPool {
    private final BlockingQueue<ThreadPoolTask> taskQueue;
    private final List<ExecutorThread> executors;
    private final int numThreads;
    private final AtomicInteger queueSize = new AtomicInteger(0);
    private volatile boolean running = false;

    public ThreadPool(int numThreads) {
        this.numThreads = numThreads;
        this.taskQueue = new LinkedBlockingQueue<>();
        this.executors = new ArrayList<>(numThreads);
    }

    /**
     * Запуск ThreadPool - создание потоков-исполнителей.
     */
    public void start() {
        running = true;

        // Создание и запуск потоков-исполнителей
        for (int i = 0; i < numThreads; i++) {
            ExecutorThread executor = new ExecutorThread();
            executors.add(executor);
            executor.start();
        }
    }

    /**
     * Метод execute добавляет задачу в очередь выполнения.
     * Этот метод вызывается Worker'ами для постановки задач в очередь.
     */
    public void execute(ThreadPoolTask task) {
        if (!running) {
            throw new IllegalStateException("Thread pool is not running");
        }

        taskQueue.add(task);
        queueSize.incrementAndGet();
    }

    /**
     * Получение текущего размера очереди задач.
     */
    public int getQueueSize() {
        return queueSize.get();
    }

    /**
     * Корректное завершение работы ThreadPool.
     */
    public void shutdown() {
        running = false;

        for (ExecutorThread executor : executors) {
            executor.stopExecutor();
        }
    }

    /**
     * Внутренний класс для потоков, выполняющих задачи из очереди.
     * Эти потоки не видны извне ThreadPool.
     */
    private class ExecutorThread extends Thread {
        private volatile boolean executorRunning = true;

        @Override
        public void run() {
            while (executorRunning && running) {
                try {
                    ThreadPoolTask task = taskQueue.take();
                    queueSize.decrementAndGet();

                    // Выполнение задачи
                    if (task != null) {
                        try {
                            task.execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        public void stopExecutor() {
            executorRunning = false;
            interrupt();
        }
    }
}