package com.wordpress.herovickers.omup.api;

import android.app.Application;
import android.content.Context;

import com.wordpress.herovickers.omup.api.interfaces.ApplicationContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class ApplicationModule {

    private Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    @ApplicationContext
    public Context provideContext() {
        return this.application;
    }



}
