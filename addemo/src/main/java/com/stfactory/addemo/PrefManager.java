package com.stfactory.addemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public final class PrefManager {

    private SharedPreferences mSharedPreferences;

    public PrefManager(Context context) {

        // Only default preferences can be used with Preference Screen
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // Get Shared Preferences with custom name
    public PrefManager(Context context, String name, int mode) {
        mSharedPreferences = context.getSharedPreferences(name, mode);
    }

    /*
     * Retrieve Values
     */

    public int getInt(String key, int defValue) {
        return mSharedPreferences.getInt(key, defValue);
    }

    public String getString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return mSharedPreferences.getFloat(key, defValue);
    }


    public boolean getBoolean(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    /*
     * Store Values
     */

    public void putString(String key, String value) {
        Editor editor = mSharedPreferences.edit();
        System.out.println("PreferenceManager putString() key: " + key + ", value: " + value);
        editor.putString(key, value);
        editor.apply();
    }

    public void putInt(String key, int value) {
        Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void putBoolean(String key, boolean value) {
        Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void putFloat(String key, float value) {
        Editor editor = mSharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

}
