package com.bowstringllp.spitack;

import android.app.Application;
import android.content.SharedPreferences;

import com.bowstringllp.spitack.components.DaggerNetComponent;
import com.bowstringllp.spitack.components.NetComponent;
import com.bowstringllp.spitack.modules.AppModule;
import com.bowstringllp.spitack.modules.NetModule;

import javax.inject.Inject;

/**
 * Created by rishabhjain on 1/31/16.
 */
public class MyApplication extends Application {
    @Inject
    SharedPreferences preferences;

    private NetComponent mNetComponent;
    private static MyApplication self;


    @Override
    public void onCreate() {
        super.onCreate();
        self = this;

        createNewNetComponent();

        // Lets the LayerClient track Application state for connection and notification management.
      //  LayerClient.applicationCreated(this);
    }

    public static MyApplication getInstance() {
        return self;
    }

    public NetComponent getNetComponent() {
        return mNetComponent;
    }

    public void createNewNetComponent() {
        mNetComponent = null;

        mNetComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule())
                .build();


        mNetComponent.inject(this);
    }
}