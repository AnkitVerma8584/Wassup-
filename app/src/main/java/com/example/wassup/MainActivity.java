package com.example.wassup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                auth= FirebaseAuth.getInstance();
                firebaseUser=auth.getCurrentUser();
                if(firebaseUser!=null)
                {
                    Intent i=new Intent(MainActivity.this,Chat_page.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Intent i=new Intent(MainActivity.this,RegisterActivity.class);
                    startActivity(i);
                    finish();
                }


            }
        },2000);


    }
}
