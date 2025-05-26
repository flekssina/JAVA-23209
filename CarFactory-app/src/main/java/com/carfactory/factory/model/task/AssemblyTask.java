package com.carfactory.factory.model.task;

import com.carfactory.config.FactoryConfig;
import com.carfactory.factory.model.Accessory;
import com.carfactory.factory.model.Auto;
import com.carfactory.factory.model.Body;
import com.carfactory.factory.model.Motor;
import com.carfactory.factory.model.storage.AccessoryStorage;
import com.carfactory.factory.model.storage.AutoStorage;
import com.carfactory.factory.model.storage.BodyStorage;
import com.carfactory.factory.model.storage.MotorStorage;
import com.carfactory.threadpool.ThreadPoolTask;

public class AssemblyTask implements ThreadPoolTask {
    private final BodyStorage bodyStorage;
    private final MotorStorage motorStorage;
    private final AccessoryStorage accessoryStorage;
    private final AutoStorage autoStorage;

    public AssemblyTask(BodyStorage bodyStorage, MotorStorage motorStorage,
                        AccessoryStorage accessoryStorage, AutoStorage autoStorage) {
        this.bodyStorage = bodyStorage;
        this.motorStorage = motorStorage;
        this.accessoryStorage = accessoryStorage;
        this.autoStorage = autoStorage;
    }

    @Override
    public void execute() {
        try {
            // Получение деталей из складов
            Body body = bodyStorage.get();
            Motor motor = motorStorage.get();
            Accessory accessory = accessoryStorage.get();

            // Имитация времени сборки
            Thread.sleep(FactoryConfig.getInstance().getWorkerDelay());

            // Создание автомобиля и помещение на склад
            Auto auto = new Auto(body, motor, accessory);
            autoStorage.put(auto);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}