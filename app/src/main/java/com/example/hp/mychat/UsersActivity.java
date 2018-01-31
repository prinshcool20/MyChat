package com.example.hp.mychat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {
    private Toolbar mtoolbar;

    private RecyclerView muserlist;
    private DatabaseReference mUserDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);




        mtoolbar=findViewById(R.id.user_appbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users");

        muserlist=findViewById(R.id.user_list);
        muserlist.setHasFixedSize(true);
        muserlist.setLayoutManager(new LinearLayoutManager(this));


    }




    @Override
    protected void onStart() {
        super.onStart();

       FirebaseRecyclerAdapter<Users,UserViewHolder> FBRA= new FirebaseRecyclerAdapter<Users, UserViewHolder>(
               Users.class,
               R.layout.user_single_layout,
               UserViewHolder.class,
               mUserDatabase
       ) {
           @Override
           protected void populateViewHolder(UserViewHolder viewHolder, Users model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setUserImage(model.getThumb_image(),getApplicationContext());

                final String user_id=getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profil_intent= new Intent(UsersActivity.this,ProfilActivity.class);
                        profil_intent.putExtra("User_id",user_id);
                        startActivity(profil_intent);
                    }
                });
           }
       };
       muserlist.setAdapter(FBRA);
    }
    public static class  UserViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public UserViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setName(String name) {
            TextView userNameView=mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }
        public void setStatus(String status){
            TextView userStatesView=mView.findViewById(R.id.user_single_status);
            userStatesView.setText(status);
        }
        public void setUserImage(String thumb_image, Context ctx){
            CircleImageView userImageView=mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.mipmap.profilpic).into(userImageView);
        }
    }
}
