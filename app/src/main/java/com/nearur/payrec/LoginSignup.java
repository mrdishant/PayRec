package com.nearur.payrec;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

import org.w3c.dom.Document;

import info.hoang8f.widget.FButton;


public class LoginSignup extends AppCompatActivity implements View.OnClickListener{

    private static final int RC_SIGN_IN = 7567;
    private FButton login;
    private GoogleSignInButton google;
    private MaterialEditText email,password;
    private TextView signup;


    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private GoogleSignInClient signInClient;
    private GoogleSignInOptions gso;

    private ProgressBar progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        inittoolbar();
        initshimmer();
        initviews();




    }

    private void initviews() {
        login=(FButton)findViewById(R.id.login);
        google=(GoogleSignInButton)findViewById(R.id.Googlebutton);
        email=(MaterialEditText)findViewById(R.id.email);
        password=(MaterialEditText)findViewById(R.id.password);
        signup=(TextView)findViewById(R.id.newaccount);
        progressBar=(ProgressBar)findViewById(R.id.progress);

        signup.setOnClickListener(this);
        google.setOnClickListener(this);
        login.setOnClickListener(this);


        auth=FirebaseAuth.getInstance();

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(auth.getCurrentUser()!=null){
                    finish();
                }
            }
        };

        signInClient= GoogleSignIn.getClient(getApplicationContext(),gso);


        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    email.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void inittoolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar12);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        toolbar.setTitle("Login");
        toolbar.setNavigationIcon(R.drawable.arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Back Pressed",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        setSupportActionBar(toolbar);

    }

    private void initshimmer() {
        ShimmerFrameLayout container =
                (ShimmerFrameLayout) findViewById(R.id.sssss);
        container.setDuration(3000);
        container.startShimmerAnimation();

    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onClick(View view) {

        if(view==signup){
            progressBar.setVisibility(View.VISIBLE);
            if(signup.getText().toString().equals("Already a Member Login")){

                login.setText("Login");
                signup.setText("New Here ? Create an Account");
                google.setText("Sign in with Google");

            }else {
                login.setText("Sign Up");
                signup.setText("Already a Member Login");
                google.setText("Sign up with Google");
            }
            progressBar.setVisibility(View.GONE);
        }else if(view == login){
            String e = email.getText().toString();
            String p = password.getText().toString();
            boolean b=validatefields(e,p);
            if(b){
                if(login.getText().toString().equals("Login")){
                    progressBar.setVisibility(View.VISIBLE);
                    auth.signInWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                             if(task.isSuccessful()){
                                 final DocumentReference documentReference=FirebaseFirestore.getInstance().collection("Users").document(auth.getUid());
                                 documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                     @Override
                                     public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                         DocumentSnapshot document=task.getResult();
                                         if(!document.exists()){
                                             Boss boss = new Boss();
                                             boss.setName(auth.getCurrentUser().getDisplayName());
                                             documentReference.set(boss);
                                             Toast.makeText(getApplicationContext(),"User Logged in",Toast.LENGTH_SHORT).show();
                                         }
                             }});}
                             else {
                                 Toast.makeText(getApplicationContext(),"Please Register First",Toast.LENGTH_SHORT);
                             }
                        }
                    });
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    auth.createUserWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if(task.isSuccessful()){
                                FirebaseFirestore.getInstance().collection("Users").document(auth.getUid()).set(new Boss());
                            }
                            else {
                                if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                    Toast.makeText(getApplicationContext(),"Already Registered Just Login",Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                }

            }

        }else if(view == google){
            signIn();
        }

    }

    private void signIn() {
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(getApplicationContext(), "Google sign in failed", Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        progressBar.setVisibility(View.VISIBLE);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            final DocumentReference documentReference=FirebaseFirestore.getInstance().collection("Users").document(auth.getUid());
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot document=task.getResult();
                                        if(!document.exists()){
                                            Boss boss = new Boss();
                                            boss.setName(auth.getCurrentUser().getDisplayName());
                                            documentReference.set(boss);
                                        }

                                        Toast.makeText(getApplicationContext(), "Authentication Successful ", Toast.LENGTH_SHORT);

                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Authentication Failed "+task.getException().getMessage(), Toast.LENGTH_SHORT);

                        }
                    }
                });
    }

    private boolean validatefields(String e, String p) {

        if(TextUtils.isEmpty(e)){
            email.setError("Email is Required");
            email.requestFocus();
            return false;
        }
        if(TextUtils.isEmpty(p)){
            password.setError("Password is Required");
            password.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(e).find()){
            email.setError("Valid Email is Required");
            email.requestFocus();
            return false;
        }
        if(p.length()<6){
            password.setError("Minimum 6 Characters");
            password.requestFocus();
            return false;
        }

        return true;
    }
}
