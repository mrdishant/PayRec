package com.nearur.payrec;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class WorkerDetails extends AppCompatActivity implements View.OnClickListener{

    private MaterialEditText name,mobile,address,job,date, editTextid, editTexttype,salary,search;
    private CircleImageView imageView;
    private TextView advance,payment_due,personalinfo,workino,dueinfo;
    String id,type;
    private ProgressBar progressBar;
    private DocumentReference documentReference;
    Worker worker;
    ArrayList<dailyWork> dailyWorks,temp1;
    ArrayList<AdvancePay> advancePays,temp3;
    ArrayList<Attendance>attendances,temp2;
    ArrayList<Payments>payments,temp4;
    private String profileURl;
    private DetailsAdapter detailsAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_details);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        id=getIntent().getStringExtra("Id");
        type=getIntent().getStringExtra("Type");

        initviews();
        enableall(false);


        editTextid.setText(id.trim());
        editTexttype.setText(type.trim());



    }

    private void initvalues() {
        documentReference= FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid()).collection(type).document(id);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        if(documentSnapshot.exists()){
                            if(type.equals("Salaried")){
                                worker=(documentSnapshot.toObject(Salaried.class));
                            }else{
                                worker=(documentSnapshot.toObject(DailyWaged.class));
                            }
                            setvalue();
                        }else{
                            Toast.makeText(getApplicationContext(),"Some Error Occured",Toast.LENGTH_SHORT).show();
                        }
                }
            });


    }

    @Override
    protected void onStart() {
        super.onStart();
        initvalues();
    }

    private void setvalue() {

        name.setText(worker.name);
        mobile.setText(worker.mobile+"");
        date.setText(worker.date_join);
        address.setText(worker.address);
        job.setText(worker.job);

        if(type.equals("Salaried")){
            salary.setText(((Salaried) worker).salary+"");
        }else{
           salary.setVisibility(View.GONE);
        }

        if(worker.picture!=null) {
            Glide.with(getApplicationContext()).load(Uri.parse(worker.picture)).into(imageView);
        }

        advance.setText("Advance Payment : ₹ "+worker.advance_payment);
        payment_due.setText("Due Payment : ₹ "+worker.payment_due);

        advance.setOnClickListener(this);
        payment_due.setOnClickListener(this);
        findViewById(R.id.paymentsprevious).setOnClickListener(this);

    }

    private void initviews() {

        name=(MaterialEditText)findViewById(R.id.name);
        mobile=(MaterialEditText)findViewById(R.id.mobile_number);
        address=(MaterialEditText)findViewById(R.id.address);
        job=(MaterialEditText)findViewById(R.id.job);
        date=(MaterialEditText)findViewById(R.id.date);
        editTextid =(MaterialEditText)findViewById(R.id.id);
        editTexttype =(MaterialEditText)findViewById(R.id.type);
        salary =(MaterialEditText)findViewById(R.id.salary);
        search =(MaterialEditText)findViewById(R.id.search);

        advance=(TextView)findViewById(R.id.advance);
        payment_due=(TextView)findViewById(R.id.payment_due);
        editTextid.setEnabled(false);
        editTexttype.setEnabled(false);

        imageView=(CircleImageView)findViewById(R.id.profile_image_add);
        progressBar=(ProgressBar)findViewById(R.id.progress);

        personalinfo=(TextView)findViewById(R.id.personalinfo);
        workino=(TextView)findViewById(R.id.workinfo);
        dueinfo=(TextView)findViewById(R.id.dueinfo);

        dailyWorks=new ArrayList<>();
        attendances=new ArrayList<>();

        temp1=new ArrayList<>();
        temp2=new ArrayList<>();



        detailsAdapter=new DetailsAdapter(dailyWorks,type,attendances,false);

        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(WorkerDetails.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(detailsAdapter);
        search.setVisibility(View.GONE);

        personalinfo.setOnClickListener(this);
        workino.setOnClickListener(this);
        dueinfo.setOnClickListener(this);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(detailsAdapter.advance){
                    advancePays.clear();
                    if(charSequence.length()<=0){
                        advancePays.addAll(temp3);
                    }else{
                        for (AdvancePay advancePay:temp3){
                            if(advancePay.toString().toLowerCase().contains(charSequence.toString().toLowerCase())){
                                advancePays.add(advancePay);
                            }
                        }
                    }
                }
                if(detailsAdapter.pay){
                    payments.clear();
                    if(charSequence.length()<=0){
                        payments.addAll(temp4);
                    }else{
                        for (Payments payment:temp4){
                            if(payment.toString().toLowerCase().contains(charSequence.toString().toLowerCase())){
                                payments.add(payment);
                            }
                        }
                    }
                }
                if(type.equals("Salaried")){
                    attendances.clear();
                    if(charSequence.length()<=0){
                        attendances.addAll(temp2);
                    }else{
                        for (Attendance attendance:temp2){
                            if(attendance.toString().toLowerCase().contains(charSequence.toString().toLowerCase())){
                                attendances.add(attendance);
                            }
                        }
                    }
                }else{
                    dailyWorks.clear();
                    if(charSequence.length()<=0){
                        dailyWorks.addAll(temp1);
                    }else{
                        for (dailyWork dailyWork:temp1){
                            if(dailyWork.toString().toLowerCase().contains(charSequence.toString().toLowerCase())){
                                dailyWorks.add(dailyWork);
                            }
                        }
                    }
                }
                detailsAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0,1,1,"Edit");
        MenuItem menuItem=menu.getItem(0);
        menuItem.setShowAsAction(1);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getTitle().equals("Edit")){
            enableall(true);
            item.setTitle("Save");
        }else if(item.getTitle().equals("Save")){
            savedata();
            enableall(false);
            item.setTitle("Edit");
        }

        return super.onOptionsItemSelected(item);
    }

    private void savedata() {

        final String n=name.getText().toString().trim();
        final String m=mobile.getText().toString().trim();
        final String a=address.getText().toString().trim();
        final String j=job.getText().toString().trim();
        final String d=date.getText().toString().trim();
        final String s=salary.getText().toString().trim();
        if(validate(n,m,a,j,d,s)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(WorkerDetails.this);
            builder.setTitle("Adding Employee");
            builder.setMessage("Are you Sure to Change ?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                        progressBar.setVisibility(View.VISIBLE);
                        if(!worker.name.equals(n)){
                            documentReference.update("name",n);
                        }

                    if(worker.mobile != Long.parseLong(m)){
                        documentReference.update("mobile",Long.parseLong(m));
                    }

                    if(type.equals("Salaried")){

                        if(((Salaried)worker).salary != Float.parseFloat(s)){
                            documentReference.update("salary",Float.parseFloat(s));
                        }
                    }

                    if(!worker.address.equals(a)){
                        documentReference.update("address",a);
                    }

                    if(!worker.date_join.equals(d)){
                        documentReference.update("date_join",d);
                    }

                    if(!worker.job.equals(j)){
                        documentReference.update("job",j);
                    }

                    if(profileURl!= null){
                            documentReference.update("picture",profileURl);
                    }

                    progressBar.setVisibility(View.GONE);
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    return;
                }
            });

            builder.create().show();

        }

    }


    private void enableall(boolean truei) {

        name.setEnabled(truei);
        mobile.setEnabled(truei);
        address.setEnabled(truei);
        date.setEnabled(truei);
        job.setEnabled(truei);
        salary.setEnabled(truei);

        if(truei){
            imageView.setOnClickListener(this);
        }else{
            imageView.setClickable(false);
        }


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1234 ){
            if(resultCode == Activity.RESULT_OK && data!=null &&data.getData() != null){
                Glide.with(getApplicationContext()).load(data.getData()).into(imageView);
                uploadpic(data.getData());
            }else{
                Toast.makeText(getApplicationContext(),"Cancelled",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void uploadpic(Uri uri) {

        progressBar.setVisibility(View.VISIBLE);

        StorageReference storageReference= FirebaseStorage.getInstance().getReference("Workers/"+worker.id+".jpg");

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.setVisibility(View.GONE);
                profileURl =taskSnapshot.getDownloadUrl().toString();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"Some Error Occured",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean validate(String n,String m,String a,String j,String d,String s) {

        if(n.isEmpty()){
            name.setError("Name is Required");
            name.requestFocus();
            return false;
        }
        if(m.isEmpty()){
            mobile.setError("Mobile Number is Required");
            mobile.requestFocus();
            return false;
        }
        if(a.isEmpty()){
            address.setError("Address is Required");
            address.requestFocus();
            return false;
        }
        if(j.isEmpty()){
            job.setError("Job is Required");
            job.requestFocus();
            return false;
        }
        if(d.isEmpty()){
            date.setError("Date of Join is Required");
            date.requestFocus();
            return false;
        }


        if(m.length()!=10){
            mobile.setError("Valid Mobile Number is Required");
            mobile.requestFocus();
            return false;
        }

        if(type.equals("Salaried")){
            if(s.isEmpty()){
                salary.setError("Salary is Required");
                salary.requestFocus();
                return false;
            }
        }

        return true;
    }

    @Override
    public void onClick(View view) {

        if(view==personalinfo){

            if(findViewById(R.id.personal).getVisibility()==View.GONE){
                findViewById(R.id.personal).setVisibility(View.VISIBLE);
                personalinfo.setTextColor(Color.BLACK);
            }
            else{
                findViewById(R.id.personal).setVisibility(View.GONE);
                personalinfo.setTextColor(Color.rgb(0,188,243));
            }
        }

        if(view==workino){

            if(findViewById(R.id.work).getVisibility()==View.GONE){
                findViewById(R.id.work).setVisibility(View.VISIBLE);
                workino.setTextColor(Color.BLACK);
            }
            else{
                findViewById(R.id.work).setVisibility(View.GONE);
                workino.setTextColor(Color.rgb(0,188,243));
            }
        }

        if(view==dueinfo){
            if(findViewById(R.id.due).getVisibility()==View.GONE){
                findViewById(R.id.due).setVisibility(View.VISIBLE);
                dueinfo.setTextColor(Color.BLACK);
            }
            else{
                findViewById(R.id.due).setVisibility(View.GONE);
                dueinfo.setTextColor(Color.rgb(0,188,243));
            }

        }



        if(view==imageView){
            Intent intent=new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,1234);
        }

        if(view==findViewById(R.id.paymentsprevious)){

            findViewById(R.id.personal).setVisibility(View.GONE);
            findViewById(R.id.work).setVisibility(View.GONE);
            findViewById(R.id.due).setVisibility(View.VISIBLE);
            personalinfo.setTextColor(Color.rgb(0, 188, 243));
            workino.setTextColor(Color.rgb(0, 188, 243));
            dueinfo.setText("Previous Payments Details");
            dueinfo.setVisibility(View.VISIBLE);
            search.setVisibility(View.VISIBLE);

            payments=new ArrayList<>();
            temp4=new ArrayList<>();

            detailsAdapter.setPayments(payments);
            detailsAdapter.pay=true;
            detailsAdapter.advance=false;

            documentReference.collection("Payments").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    payments.clear();
                    for(DocumentSnapshot documentSnapshot:documentSnapshots){
                        payments.add(documentSnapshot.toObject(Payments.class));
                    }
                    temp4.addAll(payments);

                    detailsAdapter.notifyDataSetChanged();
                }
            });



        }

        if(view==advance){
            findViewById(R.id.personal).setVisibility(View.GONE);
            findViewById(R.id.work).setVisibility(View.GONE);
            findViewById(R.id.due).setVisibility(View.VISIBLE);


            personalinfo.setTextColor(Color.rgb(0, 188, 243));
            workino.setTextColor(Color.rgb(0, 188, 243));
            dueinfo.setText("Advance Details");
            dueinfo.setVisibility(View.VISIBLE);
            search.setVisibility(View.VISIBLE);

            advancePays=new ArrayList<>();
            temp3=new ArrayList<>();

            detailsAdapter.setAdvancePays(advancePays);
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

        if(view==payment_due){
            findViewById(R.id.personal).setVisibility(View.GONE);
            findViewById(R.id.work).setVisibility(View.GONE);
            findViewById(R.id.due).setVisibility(View.VISIBLE);

            personalinfo.setTextColor(Color.rgb(0, 188, 243));
            workino.setTextColor(Color.rgb(0, 188, 243));
            dueinfo.setVisibility(View.VISIBLE);
            dueinfo.setText("Due Details");
            search.setVisibility(View.VISIBLE);

            detailsAdapter.advance=false;
            detailsAdapter.pay=false;

            if(type.equals("Salaried")){

                documentReference.collection("Attendance").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        attendances.clear();
                        for(DocumentSnapshot documentSnapshot:documentSnapshots){
                            attendances.add(documentSnapshot.toObject(Attendance.class));
                        }
                        temp2.addAll(attendances);
                        detailsAdapter.notifyDataSetChanged();
                    }
                });


            }else{
                documentReference.collection("Works").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        dailyWorks.clear();
                        for(DocumentSnapshot documentSnapshot:documentSnapshots){
                            dailyWorks.add(documentSnapshot.toObject(dailyWork.class));
                        }
                        temp1.addAll(dailyWorks);
                        detailsAdapter.notifyDataSetChanged();
                    }
                });
            }
        }


    }
}
