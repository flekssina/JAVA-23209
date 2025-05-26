package com.carfactory.factory.model.dealer;

import com.carfactory.config.FactoryConfig;
import com.carfactory.factory.model.Auto;
import com.carfactory.factory.model.storage.AutoStorage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Dealer extends Thread {
    private final AutoStorage autoStorage;
    private final int dealerNumber;
    private volatile boolean running = true;
    private volatile int delay;

    public Dealer(AutoStorage autoStorage, int dealerNumber, int delay) {
        this.autoStorage = autoStorage;
        this.dealerNumber = dealerNumber;
        this.delay = delay;
    }

    @Override
    public void run() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        while (running) {
            try {
                Auto auto = autoStorage.get();

                // Логирование продажи, если включено
                if (FactoryConfig.getInstance().isLogSale()) {
                    String time = sdf.format(new Date());
                    System.out.println(time + ": Dealer " + dealerNumber + ": " + auto);
                }

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

    public void stopDealer() {
        running = false;
        interrupt();
    }
}