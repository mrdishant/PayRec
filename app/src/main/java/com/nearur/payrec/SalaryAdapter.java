package com.nearur.payrec;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

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
        public LinearLayout layout;

        public MyViewHolder(View view) {
            super(view);
            layout=(LinearLayout)view.findViewById(R.id.layout);
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
            //Glide.with(context).load(Uri.parse(salarieds.get(position).picture)).into(holder.circleImageView);

            Picasso.with(context).load(salarieds.get(position).picture).into(holder.circleImageView,
                    PicassoPalette.with(salarieds.get(position).picture, holder.circleImageView)
                            .use(PicassoPalette.Profile.VIBRANT_DARK)
                            .intoBackground(holder.layout)
                            .intoTextColor(holder.details)

                            /*.use(PicassoPalette.Profile.VIBRANT)
                            .intoBackground(holder.cardView, PicassoPalette.Swatch.RGB)
                            .intoTextColor(holder.details, PicassoPalette.Swatch.BODY_TEXT_COLOR)*/
            );

        }

        holder.details.setText(salarieds.get(position).name);

    }




    public int getRandomColorCode(){

        Random random = new Random();

        return Color.argb(255, random.nextInt(256), random.nextInt(256),     random.nextInt(256));

    }


    @Override
    public int getItemCount() {
        return salarieds.size();
    }
}
