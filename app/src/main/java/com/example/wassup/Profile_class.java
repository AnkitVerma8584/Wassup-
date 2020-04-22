package com.example.wassup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.HashMap;

import static java.lang.System.load;

public class Profile_class extends AppCompatActivity {

    private ImageView pic,rem_pic;
    private TextView name,status,phn,bio,ename,estatus,ebio;
    private TextView edt,save;
    Intent intent;
    boolean t =true;
    static String img_url = "";
    String id="";

    public static final int PICK_IMAGE = 1;
    private Uri imageuri;
    private StorageReference mStorageRef;
    private ProgressDialog progress;

    String usrnm="",ests="",ebi="";


    DatabaseReference reference;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_class);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        pic=findViewById(R.id.profileimage);
        name=findViewById(R.id.username);
        status=findViewById(R.id.Status);
        phn=findViewById(R.id.phone);
        bio=findViewById(R.id.about);
        rem_pic=findViewById(R.id.remove_pic);

        progress=new ProgressDialog(this);



        ename=findViewById(R.id.editusername);
        estatus=findViewById(R.id.editStatus);
        ebio=findViewById(R.id.editabout);
        edt=findViewById(R.id.change);
        edt.setTextColor(Color.BLUE);

        save=findViewById(R.id.savechange);

        mStorageRef = FirebaseStorage.getInstance().getReference().child("Profilepics");



        setmode();

        intent = getIntent();
        id=intent.getStringExtra("user");

        if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(id))
        {
            edt.setVisibility(View.INVISIBLE);
        }
        else

            edt.setVisibility(View.VISIBLE);

        loadinfo(id);

        edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edt.getText().toString().equals("Edit"))
                {
                    edt.setText("Cancel");
                    edt.setTextColor(Color.RED);
                    t = false;
                    setmode();

                }
                else
                {edt.setText("Edit");
                    edt.setTextColor(Color.BLUE);
                    t = true;
                    setmode();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usrnm=ename.getText().toString().trim();
                ests=estatus.getText().toString().trim();
                ebi=ebio.getText().toString().trim();
                if(usrnm.length()>15)
                {
                    ename.requestFocus();
                    Toast.makeText(getApplicationContext(),"Username too long\n Set within 15 characters",Toast.LENGTH_SHORT).show();
                }
                if(ests.length()>25)
                {
                    ename.requestFocus();
                    Toast.makeText(getApplicationContext(),"Username too long\n Set within 25 characters",Toast.LENGTH_SHORT).show();
                }
                else{
                    storeimage();}
            }
        });

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!t)
                {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent,"Select a picture"),PICK_IMAGE);
                }
            }
        });
        rem_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_url="https://firebasestorage.googleapis.com/v0/b/attendance-dbfaf.appspot.com/o/defaultpic.jpg?alt=media&token=15801193-87ce-4f4f-a873-f5287153c384";
                Glide.with(Profile_class.this).load(img_url).into(pic);
            }
        });
    }

    private void saveinformation(String usrname, String estatus, String ebio,String img) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(id);
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("uid",id);
        hashMap.put("username",usrname+" ");
        hashMap.put("about",ebio);
        hashMap.put("status",estatus);
        hashMap.put("phone",phn.getText().toString().trim());
        hashMap.put("image",img);

        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    t=true;
                    setmode();
                    loadinfo(id);
                    edt.setText("Edit");
                    edt.setTextColor(Color.BLUE);

                }
                else {
                    Toast.makeText(Profile_class.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void setmode() {
        if(t){
        ename.setVisibility(View.INVISIBLE);
        estatus.setVisibility(View.INVISIBLE);
        ebio.setVisibility(View.INVISIBLE);
        save.setVisibility(View.INVISIBLE);
        rem_pic.setVisibility(View.INVISIBLE);

            name.setVisibility(View.VISIBLE);
            status.setVisibility(View.VISIBLE);
            bio.setVisibility(View.VISIBLE);

        }
        else
        {
            name.setVisibility(View.INVISIBLE);
            status.setVisibility(View.INVISIBLE);
            bio.setVisibility(View.INVISIBLE);

            ename.setVisibility(View.VISIBLE);
            estatus.setVisibility(View.VISIBLE);
            ebio.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);
            rem_pic.setVisibility(View.VISIBLE);
        }
    }

    private void loadinfo(final String id) {

       try{DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AddS addS=dataSnapshot.getValue(AddS.class);
                assert addS != null;
                name.setText(addS.username);
                status.setText(addS.status);
                phn.setText(addS.phone);
                bio.setText(addS.about);

                ename.setText(addS.username);
                estatus.setText(addS.status);
                ebio.setText(addS.about);
                img_url=addS.getImage();
                try{
                Glide.with(Profile_class.this).load(img_url).into(pic);} catch (Exception e) {
                    loadinfo(id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });} catch (Exception e) {
           loadinfo(id);
       }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{if(requestCode==PICK_IMAGE)
        {
            assert data != null;
            imageuri=data.getData();
            pic.setImageURI(imageuri);

        }}
        catch (Exception e){
            Toast.makeText(Profile_class.this, "You have not selected any image.", Toast.LENGTH_SHORT).show();}

    }
    void storeimage() {//To store the pic
        try {
            if (imageuri != null) {
                final StorageReference user_profile = mStorageRef.child(usrnm+".jpg");
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
                                        while(!img_url.equals(""))
                                        {saveinformation(usrnm,ests,ebi,img_url);
                                            break;
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
                saveinformation(usrnm,ests,ebi,img_url);
            }



        }catch (Exception e)
        {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}
