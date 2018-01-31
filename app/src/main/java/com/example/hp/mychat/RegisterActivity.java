package com.example.hp.mychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText mDisplayname;
    private TextInputEditText mEmail;
    private TextInputEditText mPassword;
    private Button mCreat;
    private FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar mtoolbar;

    private ProgressDialog mRegprogress;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mtoolbar = findViewById(R.id.reg_Toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegprogress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mDisplayname = findViewById(R.id.reg_display_name);
        mEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.reg_password);
        mCreat = findViewById(R.id.reg_creat_btn);



        mCreat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Display_name = mDisplayname.getEditableText().toString();
                String Email = mEmail.getEditableText().toString();
                String password = mPassword.getEditableText().toString();
                if(TextUtils.isEmpty(password) || password.length() <6)
                {
                    mPassword.setError("You must have 6 characters in your password");
                    return;
                }

                if (!TextUtils.isEmpty(Display_name) || !TextUtils.isEmpty(Email) || !TextUtils.isEmpty(password)) {
                    mRegprogress.setTitle("Registering User");
                    mRegprogress.setMessage("Please wait while we create your account!");
                    mRegprogress.setCanceledOnTouchOutside(false);
                    mRegprogress.show();
                    registr_user(Display_name, Email, password);
                }
            }
        });
    }




    private void registr_user(final String display_name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String token_id=FirebaseInstanceId.getInstance().getToken();
                            FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
                            final String uid=current_user.getUid();
                            mDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                                        String device_token=FirebaseInstanceId.getInstance().getToken();
                                    HashMap<String,String>userMap= new HashMap<>();
                                    userMap.put("name",display_name);
                                    userMap.put("status","Hi there! i'm using MyChat app");
                                    userMap.put("image","default");
                                    userMap.put("thumb_image","default");
                                    userMap.put("device_token",device_token);
                                    userMap.put("token_id",token_id);
                                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                mRegprogress.dismiss();
                                                // Sign in success, update UI with the signed-in user's information
                                                Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(mainIntent);
                                                finish();
                                            }
                                        }
                                    });
                                } else {
                            // If sign in fails, display a message to the user.
                            mRegprogress.hide();

                            Toast.makeText(RegisterActivity.this, "Please check the form and try again",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });
    }
}
