package com.nearur.payrec;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Advance extends AppCompatActivity {

    private TextView job,address,advance,payment_due;
    private CheckedTextView name;
    private Button mobile;
    private CircleImageView imageView;
    private MaterialEditText editText;
    private Worker worker;
    private ProgressBar progressBar;
    private DocumentReference documentReference;
    private RecyclerView recyclerView;
    private DetailsAdapter detailsAdapter;
    String id,type;
    private Button add;
    ArrayList<AdvancePay> advancePays,temp3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance);

        job=(TextView)findViewById(R.id.job);
        address=(TextView)findViewById(R.id.address);
        name=(CheckedTextView) findViewById(R.id.name);
        advance=(TextView)findViewById(R.id.advancepay);
        payment_due=(TextView) findViewById(R.id.payment_due);
        add=(Button)findViewById(R.id.addadvance);
        mobile=(Button)findViewById(R.id.mobile);

        id=getIntent().getStringExtra("Id");

        type=getIntent().getStringExtra("Type");

        advancePays=new ArrayList<>();
        temp3=new ArrayList<>();

        detailsAdapter=new DetailsAdapter(null,type,null,true);
        detailsAdapter.setAdvancePays(advancePays);

        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(Advance.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(detailsAdapter);



        imageView=(CircleImageView)findViewById(R.id.profile_image_add);

        editText=(MaterialEditText)findViewById(R.id.advance);

        progressBar=(ProgressBar)findViewById(R.id.progress);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(charSequence.length()>0){
                       add.setText("Pay ₹"+charSequence);
                    }
                    else {
                        add.setText("Pay");
                    }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });




        advance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.VISIBLE);
                findViewById(R.id.amounttaker).setVisibility(View.GONE);

                detailsAdapter.advance=true;
                detailsAdapter.pay=false;

                documentReference.collection("Advance").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        advancePays.clear();
                        for(DocumentSnapshot documentSnapshot:documentSnapshots){
                            advancePays.add(documentSnapshot.toObject(AdvancePay.class));
                        }
                        temp3.addAll(advancePays);
                        detailsAdapter.notifyDataSetChanged();
                    }
                });



            }
        });

        findViewById(R.id.addadvance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString().length()>0){
                    progressBar.setVisibility(View.VISIBLE);
                    AdvancePay advancePay=new AdvancePay();
                    advancePay.amount=Integer.parseInt(editText.getText().toString().trim());
                    SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                    advancePay.date=dateFormat.format(new Date()).toString();
                    advancePay.approver=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    documentReference.update("advance_payment",worker.advance_payment+Integer.parseInt(editText.getText().toString().trim()));
                    documentReference.collection("Advance").add(advancePay).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            final  DocumentReference bossrefer = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid());
                            bossrefer.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    progressBar.setVisibility(View.GONE);
                                    bossrefer.update("total_advance",task.getResult().getDouble("total_advance")+Float.parseFloat(editText.getText().toString()));
                                    Toast.makeText(getApplicationContext(),"Record Updated",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"Please Enter Amount",Toast.LENGTH_SHORT).show();
                }
            }
        });

        documentReference= FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(type).document(id);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                worker=documentSnapshot.toObject(Worker.class);

               if(worker.picture !=null){
                   Glide.with(getApplicationContext()).load(Uri.parse(worker.picture)).centerCrop().into(imageView);
               }

                name.setText(worker.name);
                advance.setText("Advance Given : ₹ "+worker.advance_payment);
                mobile.setText(worker.mobile+"");
                address.setText(worker.address);
                job .setText(worker.job);
                payment_due.setText("Work Done : ₹ "+worker.payment_due);


            }
        });


    }

    public void onBackPressed() {
        if(findViewById(R.id.amounttaker).getVisibility()==View.GONE){
            findViewById(R.id.amounttaker).setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else {
            finish();
        }
    }
}
