package com.example.wassup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    private CountryCodePicker countryCodePicker;
    EditText number, cd;
    Button next, nxt;
    TextView resend;
    String num, id, cod;
    private FirebaseAuth mAuth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        countryCodePicker = findViewById(R.id.ccp);
        number = findViewById(R.id.phone);
        next = findViewById(R.id.getcode);
        cd = findViewById(R.id.code);
        nxt = findViewById(R.id.verifycode);
        resend = findViewById(R.id.rsd);
        countryCodePicker.registerCarrierNumberEditText(number);


        mAuth = FirebaseAuth.getInstance();


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(number.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Enter a number", Toast.LENGTH_LONG).show();
                } else {
                    num = countryCodePicker.getFullNumberWithPlus().replace(" ", "");
                    SharedPreferences.Editor obj = getSharedPreferences("Phone number", MODE_PRIVATE).edit();
                    obj.putString("Number", num);
                    obj.apply();
                    Verifynumber();
                    cd.requestFocus();
                    next.setEnabled(false);
                }
            }
        });
        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cod = cd.getText().toString().trim();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, cod);
                signInWithPhoneAuthCredential(credential);
            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Verifynumber();
            }
        });

    }

    void Verifynumber() {
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                resend.setText("" + millisUntilFinished / 1000);
                resend.setEnabled(false);

            }

            @Override
            public void onFinish() {
                resend.setText("Resend");
                resend.setEnabled(true);
            }
        }.start();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                num,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        RegisterActivity.this.id = id;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Successfully logged in", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, Dashboard.class));
                            finish();
                            FirebaseUser user = task.getResult().getUser();

                        } else {
                            Toast.makeText(getApplicationContext(), "Verification failed", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }
}