package com.carfactory.factory.model.supplier;

import com.carfactory.factory.model.Accessory;
import com.carfactory.factory.model.storage.AccessoryStorage;

public class AccessorySupplier extends Supplier<Accessory> {

    public AccessorySupplier(AccessoryStorage storage, int delay) {
        super(storage, delay);
    }

    @Override
    protected Accessory createDetail() {
        return new Accessory();
    }
}