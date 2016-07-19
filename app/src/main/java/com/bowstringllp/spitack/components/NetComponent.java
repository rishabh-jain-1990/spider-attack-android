package com.bowstringllp.spitack.components;


import com.bowstringllp.spitack.MyApplication;
import com.bowstringllp.spitack.model.Bee;
import com.bowstringllp.spitack.model.Spider;
import com.bowstringllp.spitack.modules.AppModule;
import com.bowstringllp.spitack.modules.NetModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {
    void inject(MyApplication myApplication);

    void inject(Spider spider);

    void inject(Bee bee);
}