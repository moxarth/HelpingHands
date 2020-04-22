package com.example.helpinghands;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final User user = new User(Splash.this);
        if(user.getFName() == ""){
            Log.v("Init","Checking Initially");
            Intent in = new Intent(Splash.this, login.class);
            startActivity(in);
        }
        else{
            Intent in = new Intent(Splash.this, MainActivity.class);
            startActivity(in);
        }
    }
}
