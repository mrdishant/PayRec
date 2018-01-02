package com.nearur.payrec;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;
import info.hoang8f.widget.FButton;

public class Profile extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_CODE = 1234 ;
    private CircleImageView  imageView;
    private MaterialEditText editText;
    private FButton submit;
    private StorageReference storageReference;
    private FirebaseUser user;
    private Uri url;
    private ProgressBar progressBar;
    private String profileurl;
    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initoolbar();
        initviews();



    }

    private void initviews() {

        imageView=(CircleImageView)findViewById(R.id.profile_image);
        submit=(FButton)findViewById(R.id.submitpic);
        editText=(MaterialEditText)findViewById(R.id.name);
        user= FirebaseAuth.getInstance().getCurrentUser();
        progressBar=(ProgressBar)findViewById(R.id.progressprofile);
        storageReference= FirebaseStorage.getInstance().getReference("ProfilePics/"+user.getUid());

        if(user.getPhotoUrl() !=null){
            Glide.with(getApplicationContext()).load(user.getPhotoUrl()).centerCrop().into(imageView);
            profileurl = user.getPhotoUrl().toString();
            editText.setText(user.getDisplayName()+"");
        }

        submit.setOnClickListener(this);
        imageView.setOnClickListener(this);

    }

    private void initoolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Profile");
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



    }


    @Override
    public void onClick(View view) {
        if(view == imageView){
            openchooser();
        }
        else if(view == submit){
            name=editText.getText().toString();
            progressBar.setVisibility(View.VISIBLE);
            if(profileurl == null ){
                Toast.makeText(getApplicationContext(),"Please Select Picture",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(name)){
                Toast.makeText(getApplicationContext(),"Please Enter Name",Toast.LENGTH_SHORT).show();
            }else{
                savedata();
            }

        }
    }

    private void savedata() {
        UserProfileChangeRequest request=new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(Uri.parse(profileurl)).build();

        user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                    if(task.isSuccessful()){
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"Some Error Occured Please Try Again",Toast.LENGTH_SHORT).show();
                    }
            }
        });

    }

    private void openchooser() {

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data!=null && data.getData() != null){
            Glide.with(getApplicationContext()).load(data.getData()).centerCrop().into(imageView);
            url=data.getData();
            uploadpic();
        }else if(requestCode == REQUEST_CODE && resultCode == RESULT_CANCELED){
            Toast.makeText(getApplicationContext(),"Picture not Selected",Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadpic() {
        progressBar.setVisibility(View.VISIBLE);
        storageReference.putFile(url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.setVisibility(View.GONE);
                profileurl=taskSnapshot.getDownloadUrl().toString();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"Error : "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
