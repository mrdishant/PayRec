package com.nearur.payrec;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class TodayWorks extends AppCompatActivity implements TextWatcher{

    private TextView job,address,advance,payment_due;
    private CheckedTextView name;
    private Button mobile;
    private CircleImageView imageView;
    private RecyclerView recyclerView;
    private DetailsAdapter detailsAdapter;
    ArrayList<dailyWork> dailyWorks,temp3;
    private MaterialEditText quantity,price,total,comment;
    private  DocumentReference documentReference;
    String id;
    DocumentReference bossrefer;
    DailyWaged waged;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_works);

        quantity=(MaterialEditText)findViewById(R.id.units);
        price=(MaterialEditText)findViewById(R.id.price_pre_unit);
        total=(MaterialEditText)findViewById(R.id.total);
        comment=(MaterialEditText)findViewById(R.id.comment);
        imageView=(CircleImageView)findViewById(R.id.profile_image_add);
        job=(TextView)findViewById(R.id.job);
        address=(TextView)findViewById(R.id.address);
        name=(CheckedTextView) findViewById(R.id.name);
        advance=(TextView)findViewById(R.id.advancepay);
        payment_due=(TextView) findViewById(R.id.payment_due);

        mobile=(Button)findViewById(R.id.mobile);

        id=getIntent().getStringExtra("Id");


        dailyWorks=new ArrayList<>();
        temp3=new ArrayList<>();

        detailsAdapter=new DetailsAdapter(dailyWorks,"DailyWaged",null,false);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(TodayWorks.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(detailsAdapter);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });





        total.setEnabled(false);
        progressBar=(ProgressBar)findViewById(R.id.progress);

        id=getIntent().getStringExtra("Id");


        documentReference = FirebaseFirestore.getInstance()
                .collection("Users").document(FirebaseAuth.getInstance().getUid()).collection("DailyWaged").document(id);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                waged=documentSnapshot.toObject(DailyWaged.class);
                if(waged.picture !=null){
                    Glide.with(getApplicationContext()).load(Uri.parse(waged.picture)).into(imageView);
                }

                name.setText(waged.name);
                advance.setText("Advance Given : ₹ "+waged.advance_payment);
                mobile.setText(waged.mobile+"");
                address.setText(waged.address);
                job .setText(waged.job);
                payment_due.setText("Work Done : ₹ "+waged.payment_due);




            }
        });


        payment_due.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.VISIBLE);
                findViewById(R.id.amounttaker).setVisibility(View.GONE);

                documentReference.collection("Works").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        dailyWorks.clear();
                        for(DocumentSnapshot documentSnapshot:documentSnapshots){
                            dailyWorks.add(documentSnapshot.toObject(dailyWork.class));
                        }
                        temp3.addAll(dailyWorks);
                        detailsAdapter.notifyDataSetChanged();
                    }
                });

            }
        });



        findViewById(R.id.submit_dailyWork).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(quantity.getText().toString().isEmpty()||price.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Fill all fields",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                    dailyWork dailyWork=new dailyWork();
                    dailyWork.date=dateFormat.format(new Date()).toString();
                    dailyWork.approver=FirebaseAuth.getInstance().getCurrentUser().getDisplayName()+"\nComment : "+comment.getText();
                    dailyWork.units=Integer.parseInt(quantity.getText().toString());
                    dailyWork.price_per_unit=Float.parseFloat(price.getText().toString());
                    dailyWork.total=Float.parseFloat(total.getText().toString());


                    documentReference.collection("Works").add(dailyWork).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            progressBar.setVisibility(View.GONE);
                            waged.payment_due=waged.payment_due+Float.parseFloat(total.getText().toString());

                            bossrefer = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid());
                            bossrefer.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    updatedue(waged);
                                    bossrefer.update("total_payment_due",task.getResult().getDouble("total_payment_due")+Float.parseFloat(total.getText().toString()));
                                    Toast.makeText(getApplicationContext(),"Record Updated",Toast.LENGTH_SHORT).show();
                                    clearall();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Some Error Occured",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        quantity.addTextChangedListener(this);
        price.addTextChangedListener(this);


    }

    private void update(Boss boss) {

        bossrefer.update("payment_due",boss.getTotal_payment_due()+Float.parseFloat(total.getText().toString()));

    }



    private void updatedue(DailyWaged waged) {

        documentReference.set(waged);

    }

    private void clearall() {

        quantity.setText(null);
        price.setText(null);
        total.setText(null);
        comment.setText(null);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int q=0;
                float p=0;

                if (!quantity.getText().toString().isEmpty()){
                   q=Integer.parseInt(quantity.getText().toString());
                }

                if (!price.getText().toString().isEmpty()){
                    p=Float.parseFloat(price.getText().toString());
                }

            total.setText(""+q*p);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onBackPressed() {
        if(findViewById(R.id.amounttaker).getVisibility()==View.GONE){
            findViewById(R.id.amounttaker).setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else {
            finish();
        }
    }
}
