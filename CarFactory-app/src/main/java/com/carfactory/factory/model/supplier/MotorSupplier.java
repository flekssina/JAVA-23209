package com.carfactory.factory.model.supplier;

import com.carfactory.factory.model.Motor;
import com.carfactory.factory.model.storage.MotorStorage;

public class MotorSupplier extends Supplier<Motor> {

    public MotorSupplier(MotorStorage storage, int delay) {
        super(storage, delay);
    }

    @Override
    protected Motor createDetail() {
        return new Motor();
    }
}