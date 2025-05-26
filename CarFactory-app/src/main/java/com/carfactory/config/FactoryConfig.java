package com.carfactory.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class FactoryConfig {
    private static FactoryConfig instance;
    private Properties properties;

    private int storageBodySize;
    private int storageMotorSize;
    private int storageAccessorySize;
    private int storageAutoSize;
    private int accessorySuppliers;
    private int workers;
    private int dealers;
    private boolean logSale;

    private int bodySupplierDelay = 1000;
    private int motorSupplierDelay = 1000;
    private int accessorySupplierDelay = 1000;
    private int dealerDelay = 1000;
    private int workerDelay = 1000;

    private FactoryConfig() {
        properties = new Properties();
        loadDefaultConfig();
    }

    private void loadDefaultConfig() {
        try {
            // Попытка загрузить конфигурацию из файла
            FileInputStream in = new FileInputStream("factory.properties");
            properties.load(in);
            in.close();

            // Чтение значений
            storageBodySize = Integer.parseInt(properties.getProperty("StorageBodySize", "100"));
            storageMotorSize = Integer.parseInt(properties.getProperty("StorageMotorSize", "100"));
            storageAccessorySize = Integer.parseInt(properties.getProperty("StorageAccessorySize", "100"));
            storageAutoSize = Integer.parseInt(properties.getProperty("StorageAutoSize", "100"));
            accessorySuppliers = Integer.parseInt(properties.getProperty("AccessorySuppliers", "5"));
            workers = Integer.parseInt(properties.getProperty("Workers", "10"));
            dealers = Integer.parseInt(properties.getProperty("Dealers", "20"));
            logSale = Boolean.parseBoolean(properties.getProperty("LogSale", "true"));

        } catch (IOException | NumberFormatException e) {
            // Использование значений по умолчанию, если файл не найден или возникла ошибка
            storageBodySize = 100;
            storageMotorSize = 100;
            storageAccessorySize = 100;
            storageAutoSize = 100;
            accessorySuppliers = 5;
            workers = 10;
            dealers = 20;
            logSale = true;
        }
    }

    public static synchronized FactoryConfig getInstance() {
        if (instance == null) {
            instance = new FactoryConfig();
        }
        return instance;
    }

    // Геттеры для всех параметров
    public int getStorageBodySize() {
        return storageBodySize;
    }

    public int getStorageMotorSize() {
        return storageMotorSize;
    }

    public int getStorageAccessorySize() {
        return storageAccessorySize;
    }

    public int getStorageAutoSize() {
        return storageAutoSize;
    }

    public int getAccessorySuppliers() {
        return accessorySuppliers;
    }

    public int getWorkers() {
        return workers;
    }

    public int getDealers() {
        return dealers;
    }

    public boolean isLogSale() {
        return logSale;
    }

    // Методы для изменения скорости работы поставщиков и дилеров
    public int getBodySupplierDelay() {
        return bodySupplierDelay;
    }

    public void setBodySupplierDelay(int delay) {
        this.bodySupplierDelay = delay;
    }

    public int getMotorSupplierDelay() {
        return motorSupplierDelay;
    }

    public void setMotorSupplierDelay(int delay) {
        this.motorSupplierDelay = delay;
    }

    public int getAccessorySupplierDelay() {
        return accessorySupplierDelay;
    }

    public void setAccessorySupplierDelay(int delay) {
        this.accessorySupplierDelay = delay;
    }

    public int getDealerDelay() {
        return dealerDelay;
    }

    public void setDealerDelay(int delay) {
        this.dealerDelay = delay;
    }

    public int getWorkerDelay() {
        return workerDelay;
    }

    public void setWorkerDelay(int delay) {
        this.workerDelay = delay;
    }
}