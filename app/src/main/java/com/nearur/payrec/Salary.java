package com.nearur.payrec;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class Salary extends Fragment {

    ArrayList<Salaried> salarieds;
    ArrayList<Salaried> temp;
    SalaryAdapter arrayAdapter;
    RecyclerView listView;

    public Salary() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_salary, container, false);

        listView=(RecyclerView) v.findViewById(R.id.listsalary);
        salarieds=new ArrayList<>();
        temp=new ArrayList<>();

        arrayAdapter=new SalaryAdapter(salarieds);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(),2);
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(arrayAdapter);

        listView.addOnItemTouchListener(new RecylcerListener(getContext(),new RecylcerListener.listener(){

            @Override
            public void onItemClick(View view, int position) {
               showdialog2("Salaried",position);
            }
        }
        ));

        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {

            FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Salaried")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            if (e != null) {
                                Toast.makeText(getContext(), "Some Error Occured", Toast.LENGTH_LONG).show();
                                return;
                            }
                            salarieds.clear();

                            for (DocumentSnapshot doc : documentSnapshots) {
                                salarieds.add(doc.toObject(Salaried.class));
                            }
                            temp.addAll(salarieds);
                            arrayAdapter.notifyDataSetChanged();
                        }
                    });

        }
        MaterialEditText editText=(MaterialEditText)v.findViewById(R.id.salarysearch);

        editText.setBackgroundColor(Color.TRANSPARENT);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    salarieds.clear();

                    if(charSequence.length() <=0){
                        salarieds.addAll(temp);
                    }
                    else{
                        for (Salaried s : temp){
                            if(s.name.toLowerCase().contains(charSequence.toString().toLowerCase().trim())){
                                salarieds.add(s);
                            }
                        }
                    }

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return v;
    }

    public void updatedue(boolean present, String id, float payment, final long salary){
        if(present){
            DocumentReference documentReference=FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid()).collection("Salaried").document(id);
            documentReference.update("payment_due",payment+salary/30);

            final  DocumentReference bossrefer = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid());
            bossrefer.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    bossrefer.update("total_payment_due",task.getResult().getDouble("total_payment_due")+salary/30);
                    Toast.makeText(getContext(),"Record Updated",Toast.LENGTH_SHORT).show();
                }
            });


        }else{

            DocumentReference documentReference=FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid()).collection("Salaried").document(id);
            documentReference.update("payment_due",payment-salary/30);

            final  DocumentReference bossrefer = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid());
            bossrefer.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    bossrefer.update("total_payment_due",task.getResult().getDouble("total_payment_due")-salary/30);
                    Toast.makeText(getContext(),"Record Updated",Toast.LENGTH_SHORT).show();
                }
            });


        }
    }


    public void showdialog(final int position){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Attendance");
        builder.setMessage("Today "+salarieds.get(position).name+" is ?");
        builder.setPositiveButton("Present", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final  SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
                final DocumentReference  documentReference= FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Salaried").document(salarieds.get(position).id);
                final Attendance attendance=new Attendance();
                attendance.date=dateFormat.format(new Date()).toString();
                attendance.approver=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                attendance.present=true;

                documentReference.collection("Attendance").document(dateFormat.format(new Date()).toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(final DocumentSnapshot documentSnapshot) {
                        if(!documentSnapshot.exists()){
                            documentReference.collection("Attendance").document(dateFormat.format(new Date()).toString()).set(attendance).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    updatedue(attendance.present,salarieds.get(position).id,salarieds.get(position).payment_due,salarieds.get(position).salary);
                                }
                            });
                        }else {
                            documentReference.collection("Attendance").document(dateFormat.format(new Date()).toString()).set(attendance).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if(documentSnapshot.getBoolean("present")){
                                        Toast.makeText(getContext(),"Record Updated",Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        updatedue(attendance.present,salarieds.get(position).id,salarieds.get(position).payment_due,salarieds.get(position).salary);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }).setNegativeButton("Absent", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final DocumentReference  documentReference= FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Salaried").document(salarieds.get(position).id);
                final  SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
                final Attendance attendance=new Attendance();
                attendance.date=dateFormat.format(new Date()).toString();
                attendance.approver=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                attendance.present=false;


                documentReference.collection("Attendance").document(dateFormat.format(new Date()).toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(final DocumentSnapshot documentSnapshot) {
                        if(!documentSnapshot.exists()){
                            documentReference.collection("Attendance").document(dateFormat.format(new Date()).toString()).set(attendance).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(),"Record Updated",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            documentReference.collection("Attendance").document(dateFormat.format(new Date()).toString()).set(attendance).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if(documentSnapshot.getBoolean("present")){
                                        updatedue(attendance.present,salarieds.get(position).id,salarieds.get(position).payment_due,salarieds.get(position).salary);
                                    }
                                    else{
                                        Toast.makeText(getContext(),"Record Updated",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }).create().show();

    }


    public void showdialog2(String type,final int position){

        final Dialog mydialog=new Dialog(getContext());
        mydialog.setContentView(R.layout.popup);

        TextView txtclose,due1,due2,name,details2;
        LinearLayout due,advance,payment;
        Button details;
        CircleImageView imageView;


        txtclose =(TextView) mydialog.findViewById(R.id.txtclose);
        due1 =(TextView) mydialog.findViewById(R.id.due1);
        due2 =(TextView) mydialog.findViewById(R.id.due2);
        details2 =(TextView) mydialog.findViewById(R.id.details);
        name =(TextView) mydialog.findViewById(R.id.name);
        imageView =(CircleImageView) mydialog.findViewById(R.id.profile);

        name.setText(salarieds.get(position).name);
        if(salarieds.get(position).picture!=null){
            Glide.with(getContext()).load(salarieds.get(position).picture).centerCrop().into(imageView);
        }

        details2.setText(salarieds.get(position).job+", "+salarieds.get(position).address);

        if(type.equals("Salaried")){
            due1.setText("Mark");
            due2.setText("Attendance");
        }else{
            due1.setText("Today's");
            due2.setText("Work");
        }

        due=(LinearLayout)mydialog.findViewById(R.id.due);
        advance=(LinearLayout)mydialog.findViewById(R.id.advance);
        payment=(LinearLayout)mydialog.findViewById(R.id.payment);

        due.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdialog(position);
            }
        });

        advance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),Advance.class);
                intent.putExtra("Id",salarieds.get(position).id);
                intent.putExtra("Type","Salaried");
                getContext().startActivity(intent);
            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(getContext(),Paying.class);
                intent1.putExtra("Id",salarieds.get(position).id);
                intent1.putExtra("Type","Salaried");
                getContext().startActivity(intent1);
            }
        });

        details = (Button) mydialog.findViewById(R.id.btnfollow);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydialog.dismiss();
            }
        });

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x=new Intent(getContext(),WorkerDetails.class);
                x.putExtra("Id",salarieds.get(position).id);
                x.putExtra("Type","Salaried");
                getContext().startActivity(x);

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x=new Intent(getContext(),WorkerDetails.class);
                x.putExtra("Id",salarieds.get(position).id);
                x.putExtra("Type","Salaried");
                getContext().startActivity(x);

            }
        });
        mydialog.setCancelable(false);
        mydialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mydialog.show();

    }

}
