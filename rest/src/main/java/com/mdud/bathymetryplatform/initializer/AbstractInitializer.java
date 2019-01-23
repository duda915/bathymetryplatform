package com.mdud.bathymetryplatform.initializer;

import javax.annotation.PostConstruct;

public abstract class AbstractInitializer implements Initializer {
    @Override
    public abstract void init();

    @Override
    @PostConstruct
    public void registerInitializer() {
        MainInitializer.addInitializer(this);
    }
}
