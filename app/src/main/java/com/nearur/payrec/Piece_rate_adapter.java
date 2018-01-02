package com.nearur.payrec;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.utils.ColorTemplate;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mrdis on 12/29/2017.
 */

public class Piece_rate_adapter extends RecyclerView.Adapter<Piece_rate_adapter.MyViewHolder> {

    ArrayList<DailyWaged> dailyWageds;
    Context context;

    public Piece_rate_adapter(ArrayList<DailyWaged> salarieds) {
        this.dailyWageds = salarieds;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.sample,parent,false);
        context=parent.getContext();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if(dailyWageds.get(position).picture != null){
            Glide.with(context).load(Uri.parse(dailyWageds.get(position).picture)).centerCrop().into(holder.circleImageView);
        }

        StringBuilder builder=new StringBuilder();

        builder.append("Name : "+ dailyWageds.get(position).name+"\n");
        builder.append("Advance : "+ dailyWageds.get(position).advance_payment+"\n");
        builder.append("Due : "+ dailyWageds.get(position).payment_due+"\n");


        holder.details.setText(builder.toString());


    }

    @Override
    public int getItemCount() {
        return dailyWageds.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView circleImageView;
        public TextView details;
        private CardView cardView;

        public MyViewHolder(View view) {
            super(view);

            circleImageView=(CircleImageView) view.findViewById(R.id.profile_image_add);
            details=(TextView)view.findViewById(R.id.worker_details);
            cardView=(CardView)view.findViewById(R.id.cardview);
        }

    }
}
