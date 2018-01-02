package com.nearur.payrec;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class Piecerate extends Fragment {

    ArrayList<DailyWaged> dailyWageds;
    ArrayList<DailyWaged> temp;
    Piece_rate_adapter arrayAdapter;
    RecyclerView listView;


    public Piecerate() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_salary, container, false);

        listView=(RecyclerView) v.findViewById(R.id.listsalary);
        dailyWageds =new ArrayList<>();
        temp=new ArrayList<>();



        arrayAdapter=new Piece_rate_adapter(dailyWageds);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(arrayAdapter);

        listView.addOnItemTouchListener(new RecylcerListener(getContext(), new RecylcerListener.listener() {
            @Override
            public void onItemClick(View view, final int position) {
                DialogPlus dialog = DialogPlus.newDialog(getContext())
                        .setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, new String[]{"View Details","Advance", "Today's Work","Do a Payment"}))
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(final DialogPlus dialog, Object item, View v, int i) {
                                switch(i){
                                    case 0:
                                        Intent x=new Intent(getContext(),WorkerDetails.class);
                                        x.putExtra("Id",dailyWageds.get(position).id);
                                        x.putExtra("Type","DailyWaged");
                                        getContext().startActivity(x);
                                        break;

                                    case 1:
                                        Intent intent = new Intent(getContext(), Advance.class);
                                        intent.putExtra("Id", dailyWageds.get(position).id);
                                        intent.putExtra("Type","DailyWaged");
                                        getContext().startActivity(intent);
                                        break;

                                    case 2:
                                        Intent intent1 = new Intent(getContext(), TodayWorks.class);
                                        intent1.putExtra("Id", dailyWageds.get(position).id);
                                        getContext().startActivity(intent1);
                                        break;

                                    case 3:
                                        Intent intent2=new Intent(getContext(),Paying.class);
                                        intent2.putExtra("Id",dailyWageds.get(position).id);
                                        intent2.putExtra("Type","DailyWaged");
                                        getContext().startActivity(intent2);
                                        break;

                                }
                                dialog.dismiss();
                            }
                        })
                        .setExpanded(false)// This will enable the expand feature, (similar to android L share dialog)
                        .create();
                dialog.show();
            }}));

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            return v;
        }

        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("DailyWaged")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if(e != null){
                            Toast.makeText(getContext(),"Some Error Occured",Toast.LENGTH_LONG).show();
                            return;
                        }
                        dailyWageds.clear();


                        for(DocumentSnapshot doc : documentSnapshots){
                            dailyWageds.add(doc.toObject(DailyWaged.class));
                        }
                        arrayAdapter.notifyDataSetChanged();
                        temp.clear();
                        temp.addAll(dailyWageds);

                    }
                });


        MaterialEditText editText=(MaterialEditText)v.findViewById(R.id.salarysearch);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                dailyWageds.clear();

                if (charSequence.length() <= 0) {
                    dailyWageds.addAll(temp);
                } else {
                    for (DailyWaged s : temp) {
                        if (s.name.toLowerCase().contains(charSequence.toString().toLowerCase().trim())) {
                            dailyWageds.add(s);
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

}
