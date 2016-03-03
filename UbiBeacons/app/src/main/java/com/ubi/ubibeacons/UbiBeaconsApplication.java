package com.ubi.ubibeacons;

import android.app.Application;
import android.content.Context;

import com.estimote.sdk.EstimoteSDK;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * @author João Pedro Pedrosa, UbiBeacons on 29/02/2016.
 */
public class UbiBeaconsApplication extends Application{

    //TODO - Insert the correct App ID and App Token
    public static final String ESTIMOTE_APP_ID = "";
    public static final String ESTIMOTE_APP_TOKEN = "";
    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
        EstimoteSDK.initialize(this.getApplicationContext(), ESTIMOTE_APP_ID, ESTIMOTE_APP_TOKEN);
        EstimoteSDK.enableDebugLogging(true);

        RealmConfiguration config = new RealmConfiguration.Builder(this.getApplicationContext())
                .deleteRealmIfMigrationNeeded()
                .name(this.getPackageName())
                .build();
        Realm.setDefaultConfiguration(config);
    }

    public static Context getContext() {
        return mContext;
    }
}
