package com.nearur.payrec;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.victor.loading.rotate.RotateLoading;

import de.hdodenhof.circleimageview.CircleImageView;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */


public class UserProfile extends Fragment implements View.OnClickListener {


    private static final int REQUEST_CODE = 1234;
    TextView salaried, dailywaged,edit;
    Button delete, logout;
    CircleImageView imageView;
    MaterialEditText editText;
    Boss boss;
    RotateLoading loading;
    FirebaseUser user;
    private String profileurl;
    public StorageReference storageReference;
    private String name;

    public UserProfile() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);

        salaried = (TextView) v.findViewById(R.id.salaried);
        dailywaged = (TextView) v.findViewById(R.id.dailywaged);

        delete = (Button) v.findViewById(R.id.delete);

        logout = (Button) v.findViewById(R.id.logout);
        imageView = (CircleImageView) v.findViewById(R.id.imageView);

        loading=(RotateLoading)v.findViewById(R.id.loading);
        editText = (MaterialEditText) v.findViewById(R.id.name);
        edit = (TextView) v.findViewById(R.id.edit);
        edit.setOnClickListener(this);
        delete.setOnClickListener(this);
        logout.setOnClickListener(this);
        setvalues();


        return v;
    }

    private void setvalues() {

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            if (user.getPhotoUrl() != null) {
                profileurl=user.getPhotoUrl().toString();
                Glide.with(getContext()).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(imageView);
            }
            final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    boss = documentSnapshot.toObject(Boss.class);
                    editText.setText(boss.name);
                }
            });


            documentReference.collection("Salaried").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    salaried.setText("Total : " + documentSnapshots.size());
                }
            });


            documentReference.collection("DailyWaged").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    dailywaged.setText("Total : " + documentSnapshots.size());
                }
            });
        }
    }

    public void onClick(View view) {
        if (view == imageView) {
            openchooser();
        } else if (view == logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(),LoginSignup.class));
            getActivity().finish();
        }else if(view == edit){
            if(edit.getText().equals("Edit")){
                editText.setEnabled(true);
                edit.setText("Save");
                imageView.setOnClickListener(this);
            }
            else{
                savedata();
            }

        }
    }

    private void savedata() {
        name = editText.getText().toString();
        loading.start();
        if(profileurl !=null) {
            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(Uri.parse(profileurl)).build();

            user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    loading.stop();
                    if (task.isSuccessful()) {
                        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid());
                        documentReference.update("name", name);
                    } else {
                        Toast.makeText(getContext(), "Some Error Occured Please Try Again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void openchooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Glide.with(getContext()).load(data.getData()).into(imageView);
            uploadpic(data.getData());
        } else if (requestCode == REQUEST_CODE && resultCode == RESULT_CANCELED) {
            Toast.makeText(getContext(), "Picture not Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadpic(Uri url) {
        loading.start();
        storageReference = FirebaseStorage.getInstance().getReference("ProfilePics/" + user.getUid());
        storageReference.putFile(url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                loading.stop();
                profileurl = taskSnapshot.getDownloadUrl().toString();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loading.stop();
                Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


