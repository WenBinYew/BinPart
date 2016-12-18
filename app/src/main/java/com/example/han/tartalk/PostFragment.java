package com.example.han.tartalk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.post_fragment,container,false);

        selectImage = (ImageButton) v.findViewById(R.id.imageButton);
        titleField = (EditText) v.findViewById(R.id.titleField);
        descField = (EditText) v.findViewById(R.id.descField);
        postBtn = (Button) v.findViewById(R.id.btnPost);
        storage = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance().getReference().child("post");
        progress = new ProgressDialog(getActivity());

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

            StorageReference filepath = storage.child("Images").child(imageUri.getLastPathSegment());

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
                    String strDate = sdf.format(c.getTime());

                    DatabaseReference newPost = database.push();
                    newPost.child("Id").setValue(newPost.getKey());
                    newPost.child("Title").setValue(title_val);
                    newPost.child("Content").setValue(desc_val);
                    newPost.child("Image").setValue(downloadUrl.toString());
                    newPost.child("Date").setValue(strDate);
                    //newPost.child("Date").push().setValue("hihih");


                    progress.dismiss();
                }
            });
        }

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK){

            imageUri = data.getData();

            selectImage.setImageURI(imageUri);
        }
    }
}
