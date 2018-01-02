package com.nearur.payrec;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.SetOptions;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


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

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        listView.setAdapter(arrayAdapter);

        listView.addOnItemTouchListener(new RecylcerListener(getContext(),new RecylcerListener.listener(){

            @Override
            public void onItemClick(View view, final int position) {
                DialogPlus dialog = DialogPlus.newDialog(getContext())
                        .setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,new String[]{"View Details","Advance","Attendance","Do Payment"}))
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(final DialogPlus dialog, Object item, View v, int i) {

                                switch(i){
                                    case 0:
                                        Intent x=new Intent(getContext(),WorkerDetails.class);
                                        x.putExtra("Id",salarieds.get(position).id);
                                        x.putExtra("Type","Salaried");
                                        getContext().startActivity(x);
                                        break;

                                    case 1:
                                        Intent intent=new Intent(getContext(),Advance.class);
                                        intent.putExtra("Id",salarieds.get(position).id);
                                        intent.putExtra("Type","Salaried");
                                        getContext().startActivity(intent);
                                        break;

                                    case 2:showdialog(position);
                                        break;

                                    case 3:
                                        Intent intent1=new Intent(getContext(),Paying.class);
                                        intent1.putExtra("Id",salarieds.get(position).id);
                                        intent1.putExtra("Type","Salaried");
                                        getContext().startActivity(intent1);
                                        break;
                                }
                                dialog.dismiss();
                            }
                        })
                        .setExpanded(false)// This will enable the expand feature, (similar to android L share dialog)
                        .create();
                dialog.show();
            }
        }
        ));

        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Salaried")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if(e != null){
                            Toast.makeText(getContext(),"Some Error Occured",Toast.LENGTH_LONG).show();
                            return;
                        }
                        salarieds.clear();

                        for(DocumentSnapshot doc : documentSnapshots){
                            salarieds.add(doc.toObject(Salaried.class));
                        }
                        temp.addAll(salarieds);
                        arrayAdapter.notifyDataSetChanged();
                }
            });


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

}
