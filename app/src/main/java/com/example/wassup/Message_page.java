package com.example.wassup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    TextView name,blck_txt;

    EditText msg;
    ImageView snd;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    FirebaseUser fuser;
    DatabaseReference reference;

    Intent intent;

    String usr_id;
    int state=1;
    boolean isblocked=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        profile_pic = findViewById(R.id.profilepic);
        name = findViewById(R.id.username);
        msg = findViewById(R.id.text);
        snd = findViewById(R.id.send);
        blck_txt=findViewById(R.id.block_text);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        final String user_id = intent.getStringExtra("userid");
        usr_id = user_id;

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        assert user_id != null;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(user_id);

        checkifIamblocked();
        load(user_id);
        checkmyblocklist();
        blck_txt.setVisibility(View.INVISIBLE);

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isblocked){
                Intent it = new Intent(Message_page.this, Profile_class.class);
                it.putExtra("user", usr_id);
                startActivity(it);}
            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isblocked){
                Intent it = new Intent(Message_page.this, Picture.class);
                it.putExtra("UID", usr_id);
                startActivity(it);
                }
            }
        });
        snd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = msg.getText().toString();
                if (!s.trim().equals(""))
                    sendMessage(fuser.getUid(), user_id, s);
                else
                    Toast.makeText(getApplicationContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
                msg.setText("");
            }
        });




    }
    void checkmyblocklist()
    {
        final  FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference r=FirebaseDatabase.getInstance().getReference("Blocks").child(firebaseUser.getUid());
        r.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    String t= snapshot.getKey();
                    assert t != null;
                    if(t.equals(usr_id))
                    {
                        lock();
                        state=-1;
                        Glide.with(Message_page.this).load("https://firebasestorage.googleapis.com/v0/b/attendance-dbfaf.appspot.com/o/defaultpic.jpg?alt=media&token=15801193-87ce-4f4f-a873-f5287153c384").into(profile_pic);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    void checkifIamblocked()
    {

        final  FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference r=FirebaseDatabase.getInstance().getReference("Blocks").child(usr_id).child(firebaseUser.getUid());
        r.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {   Person p= snapshot.getValue(Person.class);
                    assert p != null;
                    String t= p.uid;
                    assert t != null;
                    try{
                        if(t.equals(firebaseUser.getUid()))
                        {
                           lock();
                            isblocked=true;
                            return;
                        }
                        else
                            isblocked=false;
                    } catch (Exception e) {

                        Toast.makeText(getApplicationContext(),"("+t+")",Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void blockperson(String usr_id)
    {
        reference = FirebaseDatabase.getInstance().getReference("Blocks").child(fuser.getUid());
        Person person=new Person(usr_id);

        reference.child(usr_id).push().setValue(person).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    lock();
                }
                else {
                    Toast.makeText(Message_page.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    void lock()
    {
        snd.setVisibility(View.INVISIBLE);
        msg.setVisibility(View.INVISIBLE);
        blck_txt.setVisibility(View.VISIBLE);

    }
    void unblock(final String usr_id)
    { FirebaseDatabase.getInstance().getReference("Blocks").child(fuser.getUid()).child(usr_id).removeValue();
        snd.setVisibility(View.VISIBLE);
        msg.setVisibility(View.VISIBLE);
        blck_txt.setVisibility(View.INVISIBLE);

        checkifIamblocked();
    }

    //loading data

    private void load(final String user_id)
    {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AddS addS = dataSnapshot.getValue(AddS.class);
                assert addS != null;
                name.setText(addS.username);

                try{
                    if(isblocked)
                        Glide.with(Message_page.this).load("https://firebasestorage.googleapis.com/v0/b/attendance-dbfaf.appspot.com/o/defaultpic.jpg?alt=media&token=15801193-87ce-4f4f-a873-f5287153c384").into(profile_pic);
                    else
                        Glide.with(Message_page.this).load(addS.getImage()).into(profile_pic);
                } catch (Exception e) {
                   load(user_id);
                }
                readMessage(fuser.getUid(), user_id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);
    }

    private void readMessage(final String myid, final String userid) {
        mchat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Chat chat = dataSnapshot1.getValue(Chat.class);
                    assert chat != null;
                    if ((chat.getReceiver().equals(myid) && chat.getSender().equals(userid)) || (chat.getReceiver().equals(userid) && chat.getSender().equals(myid))) {
                        mchat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(Message_page.this, mchat);
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
        getMenuInflater().inflate(R.menu.user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                if(!isblocked) {
                    Intent it = new Intent(Message_page.this, Profile_class.class);
                    it.putExtra("user", usr_id);
                    startActivity(it);
                }
                else
                    Toast.makeText(getApplicationContext(),"You cannot view profile",Toast.LENGTH_SHORT).show();
                break;

            case R.id.block:
                state*=-1;
                if(state==-1)
                blockperson(usr_id);
                else if(state==1)
                    unblock(usr_id);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item=menu.findItem(R.id.block);
       if(state==-1)
           item.setTitle("Unblock");
       if(state==1)
           item.setTitle("Block");

       return true;
    }
}
