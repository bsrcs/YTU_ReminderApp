package com.busra.reminder.utils;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {

    private static String firebaseKey;

    public static DatabaseReference initFirebase(Context context) {
        FirebaseApp.initializeApp(context);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        firebaseKey = firebaseUser.getUid();

        return database.getReference().child("ReminderApp").child(firebaseKey);
    }

    public static String getFirebaseKey() {
        return firebaseKey;
    }
}
