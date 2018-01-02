package com.nearur.payrec;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mrdis on 12/28/2017.
 */

public class SalaryAdapter extends RecyclerView.Adapter<SalaryAdapter.MyViewHolder> {

    ArrayList<Salaried> salarieds;
    Context context;

    public SalaryAdapter(ArrayList<Salaried> list) {
        salarieds=list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView circleImageView;
        public TextView details;

        public MyViewHolder(View view) {
            super(view);

            circleImageView=(CircleImageView) view.findViewById(R.id.profile_image_add);
            details=(TextView)view.findViewById(R.id.worker_details);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.sample,parent,false);
        context=parent.getContext();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if(salarieds.get(position).picture != null){
            Glide.with(context).load(Uri.parse(salarieds.get(position).picture)).centerCrop().into(holder.circleImageView);
        }

        StringBuilder builder=new StringBuilder();

        builder.append("Name : "+salarieds.get(position).name+"\n");
        builder.append("Advance : "+salarieds.get(position).advance_payment+"\n");
        builder.append("Due : "+salarieds.get(position).payment_due+"\n");
        builder.append("Salary : "+salarieds.get(position).salary);

        holder.details.setText(builder.toString());

    }



    @Override
    public int getItemCount() {
        return salarieds.size();
    }
}
