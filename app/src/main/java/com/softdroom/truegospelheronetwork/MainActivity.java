package com.softdroom.truegospelheronetwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.softdroom.truegospelheronetwork.Authentication.LoginActivity;

import java.util.Date;


public class MainActivity extends android.support.v7.app.AppCompatActivity {


    private static final int SIGN_IN_REQUEST_CODE = 10;
    private FirebaseListAdapter<ChatMessage> adapter;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseDatabase FirebaseDatabase;
    private static boolean s_persistenceInitialized = false;


    FloatingActionButton fab ;
    FloatingActionButton fab1 ;
    FloatingActionButton  fab2;
    FloatingActionButton fab3;

    boolean isFABOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
              fab2 = (FloatingActionButton) findViewById(R.id.fab1);
          fab3 = (FloatingActionButton) findViewById(R.id.fab3);

//        FirebaseDatabase.setLogLevel(Logger.Level.DEBUG);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        FirebaseCrash.log("Activity created");



        FirebaseDatabase = FirebaseDatabase.getInstance();

        if (!s_persistenceInitialized) {
            FirebaseDatabase.setPersistenceEnabled(true);
            s_persistenceInitialized = true;
        }





        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            Intent mletgologin = new Intent();
            mletgologin.setClass(MainActivity.this, LoginActivity.class);
            startActivity(mletgologin);

//            startActivityForResult(
//                    AuthUI.getInstance()
//                            .createSignInIntentBuilder()
//                            .build(),
//                    SIGN_IN_REQUEST_CODE
//            );
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();

            // Load chat room contents
            displayChatMessages();
        }

//.......................................


        FloatingActionButton send =
                findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference()
                        .push()
                        .setValue(new ChatMessage(input.getText().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName())
                        );

                // Clear the input
                input.setText("");
            }
        });












        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });


fab1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {


        FirebaseAuth.getInstance().signOut();


        Intent signout = new Intent();
        signout.setClass(MainActivity.this, LoginActivity.class);
        startActivity(signout);
    }
});

        //...................
    }






    @Override
    protected void onStart() {
        super.onStart();

        //long endAt = 100L; // Fixed value: CRASH on third app restart
          long endAt = new Date().getTime(); // Dynamic value: NO CRASH
        getGoal("min_per_day", endAt, "some_uid");
    }

    private void getGoal(String p_goalId, long p_endAt, String p_uid) {
        Query ref = FirebaseDatabase.getReference("v0/data/meditation/goals").child(p_goalId).child(p_uid)
                .orderByChild("time").endAt(p_endAt).limitToLast(1);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("FB", "Snapshot: " + dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FB", "Error: " + error);
            }
        });
    }





    private void displayChatMessages() {

        ListView listOfMessages = findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = v.findViewById(R.id.message_text);
                TextView messageUser = v.findViewById(R.id.message_user);
                TextView messageTime = v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };

        listOfMessages.setAdapter(adapter);

    }






    private void showFABMenu(){

        isFABOpen=true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
    }

    private void closeFABMenu(){
        isFABOpen=false;
         fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab3.animate().translationY(0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                displayChatMessages();
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();


                // Close the app
                finish();
            }
        }

    }


}
