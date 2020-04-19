package com.example.wassup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Chat_page extends AppCompatActivity {
    private TextView detail;
    private ImageView pic;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    TextView chats,stat,users;
    ViewPager viewPager;
    PagerViewAdapter pagerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_chat_page);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        detail=findViewById(R.id.username);
        pic=findViewById(R.id.profilepic);

        chats = findViewById(R.id.chat);
        stat=findViewById(R.id.status);
        users=findViewById(R.id.user);
        viewPager=findViewById(R.id.view_pager);
        pagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerViewAdapter);
        restore();
        chats.setTextColor(Color.WHITE);
        chats.setTextSize(25f);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                onchangedtab(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);

            }
        });
        stat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);

            }
        });
        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2);

            }
        });


            firebaseAuth=FirebaseAuth.getInstance();
            firebaseUser= firebaseAuth.getCurrentUser();
            assert firebaseUser!=null;
            String s =firebaseUser.getUid();
            SharedPreferences myobj =getSharedPreferences("Phone number",MODE_PRIVATE);
            String phn=myobj.getString("Number",null);
            reference= FirebaseDatabase.getInstance().getReference().child("Users").child(s);
            showname();

    }

    private void onchangedtab(int position) {
        if(position ==0)
        {    restore();
            chats.setTextColor(Color.WHITE);
            chats.setTextSize(25f);
        }
        if(position ==1)
        {
            restore();
            stat.setTextColor(Color.WHITE);
            stat.setTextSize(25f);
        }
        if(position ==2)
        {
            restore();
            users.setTextColor(Color.WHITE);
            users.setTextSize(25f);
        }
    }

    void showname()
    {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               AddS addS=dataSnapshot.getValue(AddS.class);


                assert addS != null;
                detail.setText(addS.username);
                detail.setTextSize(25f);
                String x=addS.image;

                Glide.with(getApplicationContext()).load(x).into(pic);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void restore()
    {
        chats.setTextColor(Color.GRAY);
        chats.setTextSize(20f);
        stat.setTextColor(Color.GRAY);
        stat.setTextSize(20f);
        users.setTextColor(Color.GRAY);
        users.setTextSize(20f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
            firebaseAuth.signOut();
            startActivity(new Intent(Chat_page.this,RegisterActivity.class));
            finish();
            break;

            case R.id.prof:
                Intent it=new Intent(Chat_page.this,Profile_class.class);
                it.putExtra("user",firebaseUser.getUid());
                startActivity(it);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
