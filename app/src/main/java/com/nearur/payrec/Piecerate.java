package com.nearur.payrec;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


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

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(),2);
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(arrayAdapter);

        listView.addOnItemTouchListener(new RecylcerListener(getContext(), new RecylcerListener.listener() {
            @Override
            public void onItemClick(View view, final int position) {
                showdialog2("Dailywaged",position);
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

        name.setText(dailyWageds.get(position).name);
        if(dailyWageds.get(position).picture!=null){
            Glide.with(getContext()).load(dailyWageds.get(position).picture).centerCrop().into(imageView);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x=new Intent(getContext(),WorkerDetails.class);
                x.putExtra("Id",dailyWageds.get(position).id);
                x.putExtra("Type","DailyWaged");
                getContext().startActivity(x);
            }
        });

        details2.setText(dailyWageds.get(position).job+", "+dailyWageds.get(position).address);

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
                Intent intent1 = new Intent(getContext(), TodayWorks.class);
                intent1.putExtra("Id", dailyWageds.get(position).id);
                getContext().startActivity(intent1);
            }
        });

        advance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Advance.class);
                intent.putExtra("Id", dailyWageds.get(position).id);
                intent.putExtra("Type","DailyWaged");
                getContext().startActivity(intent);
            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2=new Intent(getContext(),Paying.class);
                intent2.putExtra("Id",dailyWageds.get(position).id);
                intent2.putExtra("Type","DailyWaged");
                getContext().startActivity(intent2);
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
                x.putExtra("Id",dailyWageds.get(position).id);
                x.putExtra("Type","DailyWaged");
                getContext().startActivity(x);

            }
        });
        mydialog.setCancelable(false);
        mydialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mydialog.show();

    }


}
