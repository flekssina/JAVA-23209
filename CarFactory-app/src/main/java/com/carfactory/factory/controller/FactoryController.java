package com.carfactory.factory.controller;

import com.carfactory.config.FactoryConfig;
import com.carfactory.factory.model.dealer.Dealer;
import com.carfactory.factory.model.storage.AccessoryStorage;
import com.carfactory.factory.model.storage.AutoStorage;
import com.carfactory.factory.model.storage.BodyStorage;
import com.carfactory.factory.model.storage.MotorStorage;
import com.carfactory.factory.model.supplier.AccessorySupplier;
import com.carfactory.factory.model.supplier.BodySupplier;
import com.carfactory.factory.model.supplier.MotorSupplier;
import com.carfactory.factory.model.task.AssemblyTask;
import com.carfactory.threadpool.*;


import java.util.ArrayList;
import java.util.List;

public class FactoryController extends Thread {
    private final FactoryConfig config = FactoryConfig.getInstance();
    private final List<Worker> workers = new ArrayList<>();
    private final BodyStorage bodyStorage;
    private final MotorStorage motorStorage;
    private final AccessoryStorage accessoryStorage;
    private final AutoStorage autoStorage;

    private final ThreadPool threadPool;

    private final BodySupplier bodySupplier;
    private final MotorSupplier motorSupplier;
    private final List<AccessorySupplier> accessorySuppliers = new ArrayList<>();
    private final List<Dealer> dealers = new ArrayList<>();

    private volatile boolean running = true;

    public FactoryController() {
        // Создание хранилищ
        bodyStorage = new BodyStorage(config.getStorageBodySize());
        motorStorage = new MotorStorage(config.getStorageMotorSize());
        accessoryStorage = new AccessoryStorage(config.getStorageAccessorySize());
        autoStorage = new AutoStorage(config.getStorageAutoSize());

        // Создание пула потоков
        threadPool = new ThreadPool(config.getWorkers());

        // Создание поставщиков
        bodySupplier = new BodySupplier(bodyStorage, config.getBodySupplierDelay());
        motorSupplier = new MotorSupplier(motorStorage, config.getMotorSupplierDelay());

        // Создание worker'ов (сборщиков)
        for (int i = 0; i < config.getWorkers(); i++) {
            workers.add(new Worker(threadPool));
        }
        for (int i = 0; i < config.getAccessorySuppliers(); i++) {
            accessorySuppliers.add(new AccessorySupplier(accessoryStorage, config.getAccessorySupplierDelay()));
        }

        // Создание дилеров
        for (int i = 0; i < config.getDealers(); i++) {
            dealers.add(new Dealer(autoStorage, i + 1, config.getDealerDelay()));
        }
    }

    public void startFactory() {
        // Запуск пула потоков
        threadPool.start();

        // Запуск поставщиков
        bodySupplier.start();
        motorSupplier.start();
        for (AccessorySupplier supplier : accessorySuppliers) {
            supplier.start();
        }

        // Запуск дилеров
        for (Dealer dealer : dealers) {
            dealer.start();
        }

        // Запуск контроллера
        start();
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Проверка наличия деталей на складах для формирования задачи
                if (bodyStorage.getSize() > 0 && motorStorage.getSize() > 0 &&
                        accessoryStorage.getSize() > 0 && autoStorage.getSize() < autoStorage.getCapacity()) {

                    // Создаем задачу сборки
                    AssemblyTask task = new AssemblyTask(bodyStorage, motorStorage, accessoryStorage, autoStorage);

                    // Выбираем случайного worker'а для добавления задачи
                    int workerIndex = (int) (Math.random() * workers.size());
                    workers.get(workerIndex).submitTask(task);
                }

                Thread.sleep(100); // Небольшая задержка для снижения нагрузки на CPU
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void setBodySupplierDelay(int delay) {
        config.setBodySupplierDelay(delay);
        bodySupplier.setDelay(delay);
    }

    public void setMotorSupplierDelay(int delay) {
        config.setMotorSupplierDelay(delay);
        motorSupplier.setDelay(delay);
    }

    public void setAccessorySupplierDelay(int delay) {
        config.setAccessorySupplierDelay(delay);
        for (AccessorySupplier supplier : accessorySuppliers) {
            supplier.setDelay(delay);
        }
    }

    public void setDealerDelay(int delay) {
        config.setDealerDelay(delay);
        for (Dealer dealer : dealers) {
            dealer.setDelay(delay);
        }
    }

    public void setWorkerDelay(int delay) {
        config.setWorkerDelay(delay);
    }

    public void shutdown() {
        running = false;
        interrupt();

        // Остановка поставщиков
        bodySupplier.stopSupplier();
        motorSupplier.stopSupplier();
        for (AccessorySupplier supplier : accessorySuppliers) {
            supplier.stopSupplier();
        }

        // Остановка дилеров
        for (Dealer dealer : dealers) {
            dealer.stopDealer();
        }

        // Остановка пула потоков
        threadPool.shutdown();
    }

    // Геттеры для получения статистики
    public BodyStorage getBodyStorage() {
        return bodyStorage;
    }

    public MotorStorage getMotorStorage() {
        return motorStorage;
    }

    public AccessoryStorage getAccessoryStorage() {
        return accessoryStorage;
    }

    public AutoStorage getAutoStorage() {
        return autoStorage;
    }

    public ThreadPool getThreadPool() {
        return threadPool;
    }
}