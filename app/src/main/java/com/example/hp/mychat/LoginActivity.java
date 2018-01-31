package com.example.hp.mychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private TextInputEditText mLoginemail;
    private TextInputEditText mLoginpass;
    private Button mLoginBtn;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mtoolbar=findViewById(R.id.log_Toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");

        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users");

        mProgressDialog=new ProgressDialog(this);

        mLoginemail=findViewById(R.id.log_email);
        mLoginpass=findViewById(R.id.log_password);
        mLoginBtn=findViewById(R.id.log_btn);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=mLoginemail.getEditableText().toString();
                String pass=mLoginpass.getEditableText().toString();

                if(!TextUtils.isEmpty(email)|| !TextUtils.isEmpty(pass)){
                    mProgressDialog.setTitle("Logging in");
                    mProgressDialog.setMessage("Please wait while we are chacking your credentials");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    LoginUser(email,pass);
                }
            }
        });
    }

    private void LoginUser(String email, String pass) {
    mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful()){
                mProgressDialog.dismiss();

                String current_id= mAuth.getCurrentUser().getUid();
                String user_token= FirebaseInstanceId.getInstance().getToken();
                mUserDatabase.child(current_id).child("device_token").setValue(user_token).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();
                    }
                });


            }else {
                mProgressDialog.hide();
                FirebaseAuthException e = (FirebaseAuthException)task.getException();
                Toast.makeText(LoginActivity.this, "Login Failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
               // Toast.makeText(LoginActivity.this, "Please check the form and try again",
                        //Toast.LENGTH_SHORT).show();

            }

        }
    });
    }
}
