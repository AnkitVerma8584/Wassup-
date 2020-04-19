package com.example.wassup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapter.MessageAdapter;
import de.hdodenhof.circleimageview.CircleImageView;

public class Message_page extends AppCompatActivity {

    CircleImageView profile_pic;
    TextView name;

    EditText msg;
    ImageView snd;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    FirebaseUser fuser;
    DatabaseReference reference;

    Intent intent;

    String usr_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_page);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        final RelativeLayout l=findViewById(R.id.msg);

        profile_pic=findViewById(R.id.profilepic);
        name=findViewById(R.id.username);
        msg=findViewById(R.id.text);
        snd=findViewById(R.id.send);

        recyclerView =findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        final String user_id=intent.getStringExtra("userid");
        usr_id=user_id;

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        assert user_id != null;
        reference= FirebaseDatabase.getInstance().getReference("Users").child(user_id);

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(Message_page.this,Profile_class.class);
                it.putExtra("user",usr_id);
                startActivity(it);
            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it =new Intent(Message_page.this,Picture.class);
                it.putExtra("UID",usr_id);
               startActivity(it);
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AddS addS=dataSnapshot.getValue(AddS.class);
                name.setText(addS.username);
                Glide.with(Message_page.this).load(addS.getImage()).into(profile_pic);
                readMessage(fuser.getUid(),user_id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        snd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=msg.getText().toString();
                if(!s.trim().equals(""))
                    sendMessage(fuser.getUid(),user_id,s);
                else
                    Toast.makeText(getApplicationContext(),"Message cannot be empty",Toast.LENGTH_SHORT).show();
                msg.setText("");
            }
        });
    }
    private void sendMessage(String sender,String receiver,String message)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);

        reference.child("Chats").push().setValue(hashMap);
    }

    private void readMessage(final String myid,final String userid)
    {
        mchat=new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    Chat chat=dataSnapshot1.getValue(Chat.class);
                    assert chat != null;
                    if((chat.getReceiver().equals(myid)&&chat.getSender().equals(userid))||(chat.getReceiver().equals(userid)&&chat.getSender().equals(myid))){
                        mchat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(Message_page.this,mchat);
                    recyclerView.setAdapter(messageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile:
                Intent it=new Intent(Message_page.this,Profile_class.class);
                it.putExtra("user",usr_id);
                startActivity(it);
                break;

            case R.id.block:
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
