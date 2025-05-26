package com.carfactory.threadpool;

/**
 * Класс Worker представляет сборщика, который создает задачу и добавляет ее в ThreadPool.
 * Worker не выполняет задачу, а только ставит её в очередь.
 */
public class Worker {
    private final ThreadPool threadPool;

    public Worker(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    /**
     * Метод для создания задачи и добавления ее в пул потоков.
     * Worker не выполняет задачу самостоятельно, а передает её ThreadPool.
     */
    public void submitTask(ThreadPoolTask task) {
        threadPool.execute(task);
    }
}