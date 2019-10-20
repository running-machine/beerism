package com.example.beerism.Login;

import android.app.Application;

import com.google.firebase.firestore.FirebaseFirestore;

public class CustomApplication extends Application {

    private static final String TAG = CustomApplication.class.getSimpleName();

    public static CustomApplication instance = null;
    private FirebaseFirestore firebaseFirestore;

    public static CustomApplication getInstance() {
        if (null == instance) {
            instance = new CustomApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public FirebaseFirestore getDbInstance() {
        return firebaseFirestore;
    }
}
