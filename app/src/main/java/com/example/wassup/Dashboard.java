package com.example.wassup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Dashboard extends AppCompatActivity {
    private ImageView profile;
    private EditText uname,abt;
    private Button save;

    public static final int PICK_IMAGE = 1;
    private Uri imageuri;
    private StorageReference mStorageRef;
    private ProgressDialog progress;
    FirebaseAuth auth;
    DatabaseReference reference;


    private String username = "",about="";
    static String g = "", img_url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        profile=findViewById(R.id.profilepic);
        uname=findViewById(R.id.username);
        abt=findViewById(R.id.about);
        save=findViewById(R.id.go);

        auth=FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Profilepics");
        progress=new ProgressDialog(this);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select a picture"),PICK_IMAGE);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username=uname.getText().toString().trim();
                about=abt.getText().toString().trim();
                if(TextUtils.isEmpty(username))
                {
                    Toast.makeText(getApplicationContext(),"PLease Enter a Username",Toast.LENGTH_LONG).show();
                    abt.requestFocus();
                }
                else
                {
                    storeimage();
                }
            }
        });

    }

    void storeimage() {//To store the pic
        try {
            if (imageuri != null) {
                final StorageReference user_profile = mStorageRef.child( username+".jpg");
                progress.setTitle("Uploading...");
                progress.show();
                progress.setCancelable(false);
                user_profile.putFile(imageuri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                user_profile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        img_url = uri.toString();// to store url of the image
                                        progress.dismiss();
                                        while(img_url!="")
                                        {savedetails(img_url);
                                          return;
                                        }
                                    }
                                });
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                progress.setMessage((int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount())+ " % Completed");
                            }
                        });
            } else {
                AlertDialog.Builder alt = new AlertDialog.Builder(this);
                alt.setTitle("Note!")
                        .setCancelable(false)
                        .setMessage("You have not selected any profile image.Do you want to proceed with a default image?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                img_url="https://firebasestorage.googleapis.com/v0/b/attendance-dbfaf.appspot.com/o/defaultpic.jpg?alt=media&token=15801193-87ce-4f4f-a873-f5287153c384";
                                savedetails(img_url);
                            }
                        });

                AlertDialog a1 = alt.create();
                a1.show();

            }
        }catch (Exception e)
        {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    void savedetails(String image)
    {
        FirebaseUser firebaseUser=auth.getCurrentUser();
        assert firebaseUser!=null;
        String uid=firebaseUser.getUid();

        SharedPreferences myobj =getSharedPreferences("Phone number",MODE_PRIVATE);
        String phn=myobj.getString("Number",null);

        reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);



        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("uid",uid);
        hashMap.put("username",username+" ");
        hashMap.put("about",about);
        hashMap.put("image",image);

        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Intent intent=new Intent(Dashboard.this,Chat_page.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(intent);
                    finish();

                }
                else {
                    Toast.makeText(Dashboard.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{if(requestCode==PICK_IMAGE)
        {
            imageuri=data.getData();
            profile.setImageURI(imageuri);
        }}
        catch (Exception e){
            Toast.makeText(Dashboard.this, "You have not selected any image.", Toast.LENGTH_SHORT).show();}

    }

}

