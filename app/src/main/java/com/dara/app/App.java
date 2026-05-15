package com.dara.app;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Manual initialization for apps-b9f24
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:1064693622553:android:0af303fcadebf576e08788")
                .setApiKey("AIzaSyD3D9xUEBnsEREH12uMrX4msmhoOSkFlCg")
                .setProjectId("apps-b9f24")
                .setDatabaseUrl("https://apps-b9f24-default-rtdb.firebaseio.com")
                .setStorageBucket("apps-b9f24.firebasestorage.app")
                .build();

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this, options);
        }

        // Disable Realtime Database offline persistence
        // Must be called ONCE before any other use of the database instance.
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(false);
        } catch (Exception e) {
            // Already set or error
        }
    }
}
