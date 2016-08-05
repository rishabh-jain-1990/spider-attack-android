package com.bowstringllp.spiderattack.modules;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;

import com.bowstringllp.spiderattack.R;
import com.bowstringllp.spiderattack.util.Constants;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.text.SimpleDateFormat;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NetModule {

    // Constructor needs one parameter to instantiate.  
    public NetModule() {
    }

    // Dagger will only look for methods annotated with @Provides
    @Provides
    @Singleton
    // Application reference must come from AppModule.class
    SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Singleton
    @Provides
    FirebaseAnalytics getFirebase(Application app) {
        return FirebaseAnalytics.getInstance(app.getApplicationContext());
    }

    @Singleton
    @Provides
    MixpanelAPI getMixPanel(Application app) {
        return MixpanelAPI.getInstance(app, app.getString(R.string.mixpanel_token));
    }

    @Provides
    @Singleton
    SimpleDateFormat provideDateFormatInstance() {
        return new SimpleDateFormat();
    }

//    @Provides
//    @Singleton
//    WrapperUser provideCurrentUser(SharedPreferences preferences) {
//        User currentUser = new User();
//        currentUser.setId(preferences.getString(Keys.USER_ID, null));
//        currentUser.setEmail(preferences.getString(Keys.USER_EMAIL, null));
//        currentUser.setName(preferences.getString(Keys.USER_NAME, null));
//        currentUser.setImagePath(preferences.getString(Keys.USER_IMAGE_URL, null));
//        // currentUser.setPhone(preferences.getString(Keys.USER_PHONE, null));
//        currentUser.setToken(preferences.getString(Keys.USER_ACCESS_TOKEN, null));
//        // currentUser.setFbId(preferences.getString(Keys.USER_FB_ID, null));
//
//        return new WrapperUser(currentUser);
//    }

    @Provides
    @Singleton
    @Named(Constants.BEE_BITMAP)
    Bitmap[] provideBeeBitmapArray(Application application) {
        Bitmap[] bitmap = new Bitmap[24];
        bitmap[0] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00000);
        bitmap[1] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00001);
        bitmap[2] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00002);
        bitmap[3] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00003);
        bitmap[4] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00004);
        bitmap[5] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00005);
        bitmap[6] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00006);
        bitmap[7] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00007);
        bitmap[8] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00008);
        bitmap[9] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00009);
        bitmap[10] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00010);
        bitmap[11] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00011);
        bitmap[12] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00012);
        bitmap[13] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00013);
        bitmap[14] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00014);
        bitmap[15] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00015);
        bitmap[16] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00016);
        bitmap[17] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00017);
        bitmap[18] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00018);
        bitmap[19] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00019);
        bitmap[20] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00020);
        bitmap[21] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00021);
        bitmap[22] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00022);
        bitmap[23] = BitmapFactory.decodeResource(application.getResources(), R.drawable.bee_00023);

        return bitmap;
    }

    @Provides
    @Singleton
    @Named(Constants.SPIDER_BITMAP)
    Bitmap[] provideSpiderBitmapArray(Application application) {
        Bitmap[] bitmap = new Bitmap[24];
        bitmap[0] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00000);
        bitmap[1] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00001);
        bitmap[2] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00002);
        bitmap[3] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00003);
        bitmap[4] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00004);
        bitmap[5] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00005);
        bitmap[6] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00006);
        bitmap[7] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00007);
        bitmap[8] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00008);
        bitmap[9] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00009);
        bitmap[10] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00010);
        bitmap[11] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00011);
        bitmap[12] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00012);
        bitmap[13] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00013);
        bitmap[14] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00014);
        bitmap[15] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00015);
        bitmap[16] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00016);
        bitmap[17] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00017);
        bitmap[18] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00018);
        bitmap[19] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00019);
        bitmap[20] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00020);
        bitmap[21] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00021);
        bitmap[22] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00022);
        bitmap[23] = BitmapFactory.decodeResource(application.getResources(), R.drawable.spider_00023);

        return bitmap;
    }
}