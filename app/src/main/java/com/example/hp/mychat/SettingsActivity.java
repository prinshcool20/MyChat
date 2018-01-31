package com.example.hp.mychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView mDisplayImage;
    private TextView mDisaplayname;
    private TextView mDisplaysatus;
    private Button mstatusbtn;
    private Button mimagebtn;
    private static final int GALLERY_PICK = 1;

    private DatabaseReference mUserDatabse;
    private FirebaseUser mcurrentuser;
    private StorageReference mStorage;
    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        mstatusbtn = findViewById(R.id.settings_status_btn);
        mimagebtn = findViewById(R.id.settings_image_btn);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDisplayImage = findViewById(R.id.settings_image);
        mDisaplayname = findViewById(R.id.settings_display_name);
        mDisplaysatus = findViewById(R.id.settings_status);

        mcurrentuser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mcurrentuser.getUid();


        mUserDatabse = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabse.keepSynced(true);
        mUserDatabse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mDisaplayname.setText(name);
                mDisplaysatus.setText(status);

                if (!image.equals("default")) {
                }
               // Picasso.with(SettingsActivity.this).load(image).placeholder(R.mipmap.profilpic).into(mDisplayImage);
                Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.mipmap.profilpic).into(mDisplayImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Picasso.with(SettingsActivity.this).load(image).placeholder(R.mipmap.profilpic).into(mDisplayImage);
                    }

                    @Override
                    public void onError() {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mstatusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Status_value = mDisplaysatus.getText().toString();

                Intent statusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
                statusIntent.putExtra("Status", Status_value);
                startActivity(statusIntent);
            }
        });
        mimagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/+");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
                /*
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);*/
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgress = new ProgressDialog(SettingsActivity.this);
                mProgress.setTitle("Uploading Image");
                mProgress.setMessage("Wait while we are uploading and prosessing the image");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();
                Uri resultUri = result.getUri();

                File thumb_filepath = new File(resultUri.getPath());

                String current_User_id = mcurrentuser.getUid();
                try {
                    Bitmap thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(70)
                            .compressToBitmap(thumb_filepath);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();


                    StorageReference filepath = mStorage.child("profil_image").child(current_User_id + ".jpg");
                    final StorageReference thumbs_filepath = mStorage.child("profil_image").child("thumbs_image").child(current_User_id + ".jpg");
                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                final String download_url = task.getResult().getDownloadUrl().toString();


                                UploadTask uploadTask = thumbs_filepath.putBytes(thumb_byte);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        final String thumb_download_url = task.getResult().getDownloadUrl().toString();
                                        Map update_Hashmap=new HashMap();
                                        update_Hashmap.put("image",download_url);
                                        update_Hashmap.put("thumb_image",thumb_download_url);


                                        if (task.isSuccessful()) {


                                            mUserDatabse.updateChildren(update_Hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        mProgress.dismiss();
                                                        Toast.makeText(SettingsActivity.this, "Sucessfully Uploaded", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(SettingsActivity.this, "Error in uploading ThumbNail", Toast.LENGTH_LONG).show();
                                            mProgress.dismiss();
                                        }
                                    }
                                });
                            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                                Exception error = result.getError();
                            }


                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }


    }

}