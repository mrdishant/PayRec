package com.nearur.payrec;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Paying extends AppCompatActivity {

    private TextView textView;
    private CircleImageView imageView;
    private MaterialEditText editText,topaid;
    private Worker worker;
    private ProgressBar progressBar;
    float newadvance,newdue;
    String type,id;
    private DocumentReference documentReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paying);

        textView=(TextView)findViewById(R.id.details);
        imageView=(CircleImageView)findViewById(R.id.profile_image_add);
        topaid=(MaterialEditText)findViewById(R.id.tobepaid);
        progressBar=(ProgressBar)findViewById(R.id.progress);
        editText=(MaterialEditText)findViewById(R.id.advance);
        id=getIntent().getStringExtra("Id");

        type=getIntent().getStringExtra("Type");


        findViewById(R.id.addadvance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString().length()>0){
                    progressBar.setVisibility(View.VISIBLE);
                    SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                    Payments payments=new Payments();
                    payments.amount_paid=Integer.parseInt(editText.getText().toString());
                    payments.approver=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    payments.date=dateFormat.format(new Date()).toString();
                    if(Float.parseFloat(topaid.getText().toString())<Integer.parseInt(editText.getText().toString())){
                        newadvance=Float.parseFloat(editText.getText().toString().trim())-Float.parseFloat(topaid.getText().toString());
                        AdvancePay advancePay=new AdvancePay();
                        advancePay.approver=payments.approver;
                        advancePay.date=payments.date;
                        advancePay.amount=Math.abs(newadvance-worker.advance_payment);
                        DocumentReference s= FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(type).document(id);
                        s.collection("Advance").add(advancePay);
                        newdue=0;
                    }
                    else{
                        newadvance=0;
                        newdue=Float.parseFloat(topaid.getText().toString())-Float.parseFloat(editText.getText().toString().trim());
                    }
                    documentReference.update("advance_payment", newadvance);
                    documentReference.update("payment_due",newdue);

                    documentReference.collection("Payments").add(payments).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            final  DocumentReference bossrefer = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid());
                            bossrefer.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    bossrefer.update("total_advance",task.getResult().getDouble("total_advance")-(worker.advance_payment-newadvance));
                                    bossrefer.update("total_payment_due",task.getResult().getDouble("total_payment_due")-(worker.payment_due-newdue));
                                    Toast.makeText(getApplicationContext(),"Record Updated",Toast.LENGTH_SHORT).show();
                                }
                            });
                            progressBar.setVisibility(View.GONE);
                            finish();

                        }
                    });


                }else{
                    Toast.makeText(getApplicationContext(),"Please Enter Amount",Toast.LENGTH_SHORT).show();
                }
            }
        });

        documentReference= FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(type).document(id);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                worker=documentSnapshot.toObject(Worker.class);
                Glide.with(getApplicationContext()).load(Uri.parse(worker.picture)).centerCrop().into(imageView);

                if(worker==null){
                    Toast.makeText(getApplicationContext(),"Please Login",Toast.LENGTH_SHORT).show();
                    finish();
                }

                textView.setText(worker.toString());

                topaid.setText(worker.payment_due-worker.advance_payment+"");


            }

        });




    }


}
