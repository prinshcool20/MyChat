package com.example.hp.mychat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class StatusActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextInputLayout mstatus;
    private Button msavebtn;

    private DatabaseReference mstatusDatabase;
    private FirebaseUser mCurrentUser;


    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=mCurrentUser.getUid();
        mstatusDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mToolbar=findViewById(R.id.status_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Your Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String Status_value=getIntent().getStringExtra("Status");

        mstatus=findViewById(R.id.status_input);
        msavebtn=findViewById(R.id.status_save_btn);

        mstatus.getEditText().setText(Status_value);

        msavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress=new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while we Save the changes");
                mProgress.show();

                String Status=mstatus.getEditText().getText().toString();
                mstatusDatabase.child("status").setValue(Status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mProgress.dismiss();
                        }else {
                            Toast.makeText(StatusActivity.this,"There are some error in saving changes",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }
}
