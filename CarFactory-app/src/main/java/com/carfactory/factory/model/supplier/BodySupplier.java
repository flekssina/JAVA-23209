package com.carfactory.factory.model.supplier;

import com.carfactory.factory.model.Body;
import com.carfactory.factory.model.storage.BodyStorage;

public class BodySupplier extends Supplier<Body> {

    public BodySupplier(BodyStorage storage, int delay) {
        super(storage, delay);
    }

    @Override
    protected Body createDetail() {
        return new Body();
    }
}