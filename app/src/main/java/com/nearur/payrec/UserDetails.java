package com.nearur.payrec;


import android.graphics.Color;
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

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;


/**
 * A simple {@link Fragment} subclass.
 */

interface navigation{
    void setposition(int position);
}
public class UserDetails extends Fragment implements View.OnClickListener {


    TextView advance,payment;
    CheckedTextView name;
    CircleImageView imageView;
    Boss boss;
    CardView login,salaried,dailywaged,add;
    LinearLayout pays;
    PieChart mChart;
    KonfettiView konfettiView;
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


        login=(CardView)view.findViewById(R.id.loginrequired);
        pays=(LinearLayout) view.findViewById(R.id.pays);
        progressBar=(ProgressBar)view.findViewById(R.id.progress);
        salaried=(CardView) view.findViewById(R.id.salaried);
        dailywaged=(CardView) view.findViewById(R.id.dailywaged);
        add=(CardView) view.findViewById(R.id.add_employee);
        mChart =(PieChart)view.findViewById(R.id.piechart);

        salaried.setOnClickListener(this);
        add.setOnClickListener(this);
        dailywaged.setOnClickListener(this);


        initauth();

        konfettiView=(KonfettiView)view.findViewById(R.id.viewKonfetti);

        konfettiView.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 3f)
                .setFadeOutEnabled(true)
                .setTimeToLive(800000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .setPosition(-50f,  850f, -50f, -50f)
                .stream(30, 500000L);



        return view;
    }

    private void initauth() {
        auth=FirebaseAuth.getInstance();
        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(auth.getCurrentUser()!=null) {

                    login.setVisibility(View.GONE);

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
            Log.i("Token",SharedPrefManager.getInstance(getContext()).getToken());
            Glide.with(UserDetails.this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(imageView);
        }

        ArrayList<PieEntry> inti =new ArrayList<>();
        PieEntry pieEntry1=new PieEntry((float) boss.getTotal_advance(),"Advance Given");
        PieEntry pieEntry2=new PieEntry((float)boss.getTotal_payment_due(),"Payment Due");
        inti.add(pieEntry1);
        inti.add(pieEntry2);

        PieDataSet dataSet=new PieDataSet(inti,"Money Analysis");



        mChart.setUsePercentValues(true);

        mChart.setExtraOffsets(5, 10, 5, 5);

        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        mChart.setDragDecelerationFrictionCoef(0.95f);


        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener




        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);


        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        mChart.setEntryLabelColor(Color.WHITE);

        mChart.setEntryLabelTextSize(12f);

        PieData pieData=new PieData(dataSet);

        mChart.setData(pieData);
        Description description=new Description();
        description.setText("Percentage Wise");
        mChart.setDescription(description);

        mChart.invalidate();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        initauth();
    }



    @Override
    public void onClick(View view) {
        navigation navigation1=(navigation) getActivity();

        if(view==salaried){
            navigation1.setposition(1);
        }else if(view==dailywaged){
            navigation1.setposition(2);
        }else if(view==add){
            navigation1.setposition(3);
        }
    }

}
