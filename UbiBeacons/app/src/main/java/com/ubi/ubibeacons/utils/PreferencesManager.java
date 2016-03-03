package com.ubi.ubibeacons.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ubi.ubibeacons.UbiBeaconsApplication;


/**
 * Created by joaoppedrosa on 23/11/2015.
 */
public class PreferencesManager {

    private static final String PREFERENCES_NAME = "com.ubibeacons";
    private static final String PREFS_STATE = "state";


    private final SharedPreferences mPref;

    private PreferencesManager() {
        mPref = UbiBeaconsApplication.getContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static PreferencesManager getPreferencesManager() {
        return new PreferencesManager();
    }

    public String getPrefsState() {
        return mPref.getString(PREFS_STATE, "");
    }

    public void setPrefsState(String value) {
        mPref.edit()
                .putString(PREFS_STATE, value)
                .apply();
    }


}
