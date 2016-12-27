package com.example.han.tartalk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by han on 26/11/2016.
 */

public class PostFragment extends android.support.v4.app.Fragment {

    private ImageButton selectImage;
    private EditText titleField;
    private EditText descField;
    private Button postBtn;
    private StorageReference storage;
    private Uri imageUri = null;
    private ProgressDialog progress;
    private static final int GALLERY_REQUEST = 1;
    private DatabaseReference database;
    private DatabaseReference databaseImages;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference users;
    private Post post;
    private DatabaseReference databaseComments;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.post_fragment,container,false);

        selectImage = (ImageButton) v.findViewById(R.id.imageButton);
        titleField = (EditText) v.findViewById(R.id.titleField);
        descField = (EditText) v.findViewById(R.id.descField);
        postBtn = (Button) v.findViewById(R.id.btnPost);
        storage = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance().getReference().child("Posts");
        databaseComments = FirebaseDatabase.getInstance().getReference().child("Comments");
        databaseImages = FirebaseDatabase.getInstance().getReference().child("Images");
        progress = new ProgressDialog(getActivity());
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        users = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        selectImage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);

            }
        });

        postBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });

        return v;

    }

    private void startPosting() {

        progress.setMessage("Posting...");
        progress.show();

        final String title_val = titleField.getText().toString().trim();
        final String desc_val = descField.getText().toString().trim();

        if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && imageUri !=null){
            DatabaseReference newImageKey = databaseImages.push();
            StorageReference filepath = storage.child("Images").child(newImageKey.getKey().toString());

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
                    final String strDate = sdf.format(c.getTime());
                    final String user_id = auth.getCurrentUser().getUid();



                    final DatabaseReference newPost = database.push();

                    users.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            post = new Post(desc_val,strDate,downloadUrl.toString(),newPost.getKey(),dataSnapshot.child("Name").getValue().toString()
                            ,title_val,user_id);
//                            post.setId(newPost.getKey());
//                            post.setContent(desc_val);
//                            post.setTitle(title_val);
//                            post.setUid(user_id);
//                            post.setDate(strDate);
//                            post.setImage(downloadUrl.toString());
//                            post.setName(dataSnapshot.child("Name").getValue().toString());

                            newPost.setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        newPost.child("likes").push().setValue(user_id);
                                        newPost.child("dislikes").push().setValue(user_id);
                                        newPost.child("comments").push().setValue(user_id);
//                                        final DatabaseReference newComment = databaseComments.push();
//                                        newComment.setValue("testing");
//                                        newPost.child("comments").setValue(newComment.getKey());
                                        progress.dismiss();
                                        HomeFragment fragment = new HomeFragment();
                                        getFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();

                                    }
                                }
                            });


//                            newPost.child("Id").setValue(newPost.getKey());
//                            newPost.child("Uid").setValue(user_id);
//                            newPost.child("Title").setValue(title_val);
//                            newPost.child("Content").setValue(desc_val);
//                            newPost.child("Image").setValue(downloadUrl.toString());
//                            newPost.child("Date").setValue(strDate);
//                            newPost.child("Name").setValue(dataSnapshot.child("Name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if(task.isSuccessful()){
//                                        progress.dismiss();
//                                        HomeFragment fragment = new HomeFragment();
//                                        getFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
//
//                                    }
//                                }
//                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    //newPost.child("Date").push().setValue("hihih");



                }
            });
        }

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK){

            imageUri = data.getData();
            Picasso.with(getContext())
                    .load(imageUri)
                    .resize(450, 450)
                    .centerCrop()
                    .into(selectImage);
            //selectImage.setImageURI(imageUri);
        }
    }
}
