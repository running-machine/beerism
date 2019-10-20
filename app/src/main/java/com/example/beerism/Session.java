package com.example.beerism;

import android.content.Context;
import android.preference.PreferenceManager;

public class Session {
    static final String PREF_USER_NAME = null;

    static Session getSeesion(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

}
