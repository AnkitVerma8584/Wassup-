package com.example.wassup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
