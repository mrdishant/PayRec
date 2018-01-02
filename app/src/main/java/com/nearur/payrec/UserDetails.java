package com.nearur.payrec;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetails extends Fragment {


    TextView advance,payment;
    CheckedTextView name;
    CircleImageView imageView;
    Boss boss;
    CardView profile,login;
    LinearLayout pays;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    public UserDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_user_details, container, false);

        advance=(TextView)view.findViewById(R.id.advance);
        payment=(TextView)view.findViewById(R.id.payment_due);

        name=(CheckedTextView) view.findViewById(R.id.name);
        imageView=(CircleImageView)view.findViewById(R.id.profile_image);


        profile=(CardView) view.findViewById(R.id.profilecard);
        login=(CardView)view.findViewById(R.id.loginrequired);
        pays=(LinearLayout) view.findViewById(R.id.pays);
        progressBar=(ProgressBar)view.findViewById(R.id.progress);

        initauth();


        return view;
    }

    private void initauth() {
        auth=FirebaseAuth.getInstance();
        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(auth.getCurrentUser()!=null) {

                    login.setVisibility(View.GONE);
                    profile.setVisibility(View.VISIBLE);
                    pays.setVisibility(View.VISIBLE);

                    DocumentReference s = FirebaseFirestore.getInstance().collection("Users").document(auth.getUid());
                    s.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                            if (documentSnapshot.exists()) {
                                boss=documentSnapshot.toObject(Boss.class);
                                setValues();
                            }
                        }
                    });

                }
                else {
                    login.setVisibility(View.VISIBLE);
                    profile.setVisibility(View.INVISIBLE);
                    pays.setVisibility(View.INVISIBLE);
                }

            }
        };

        auth.addAuthStateListener(authStateListener);

    }

    private void setValues() {

        name.setText(boss.name);
        advance.setText("₹ "+boss.total_advance);
        payment.setText("₹ "+boss.total_payment_due);

        if(auth.getCurrentUser().getPhotoUrl()!=null && UserDetails.this.isAdded()){
            progressBar.setVisibility(View.VISIBLE);
            Glide.with(UserDetails.this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).centerCrop().into(imageView);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        initauth();
    }


}
