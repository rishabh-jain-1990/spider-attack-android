package com.bowstringllp.spiderattack.components;


import com.bowstringllp.spiderattack.MyApplication;
import com.bowstringllp.spiderattack.model.Bee;
import com.bowstringllp.spiderattack.model.Spider;
import com.bowstringllp.spiderattack.modules.AppModule;
import com.bowstringllp.spiderattack.modules.NetModule;
import com.bowstringllp.spiderattack.ui.GameFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {
    void inject(MyApplication myApplication);

    void inject(Spider spider);

    void inject(Bee bee);

    void inject(GameFragment gameFragment);
}