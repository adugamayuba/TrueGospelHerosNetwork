package com.softdroom.tghn;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import co.chatsdk.core.error.ChatSDKException;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.session.Configuration;
import co.chatsdk.firebase.FirebaseNetworkAdapter;
import co.chatsdk.firebase.file_storage.FirebaseFileStorageModule;
import co.chatsdk.ui.manager.BaseInterfaceAdapter;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase mFirebaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(this);
        mFirebaseInstance = FirebaseDatabase.getInstance(firebaseApp);



        Context context = getApplicationContext();

        try {
            // Create a new configuration
            Configuration.Builder builder = new Configuration.Builder(context);

            // Perform any other configuration steps (optional)
            builder.firebaseRootPath("prod");

            // Initialize the Chat SDK
            ChatSDK.initialize(builder.build(), new FirebaseNetworkAdapter(), new BaseInterfaceAdapter(context));

            // File storage is needed for profile image upload and image messages
            FirebaseFileStorageModule.activate();

            // Push notification module
           // FirebasePushModule.activate();

            // Activate any other modules you need.
            // ...

        } catch (ChatSDKException e) {
            // Handle any exceptions
            e.printStackTrace();
        }
        try {
            Configuration.Builder config = new Configuration.Builder(context);
            ChatSDK.initialize(config.build(), new FirebaseNetworkAdapter(), new BaseInterfaceAdapter(context));
        } catch (ChatSDKException e) {
            e.printStackTrace();
        }

        Intent mletgo = new Intent();
        mletgo.setClass(MainActivity.this, co.chatsdk.ui.login.SplashScreenActivity.class);
        startActivity(mletgo);

    }
}
