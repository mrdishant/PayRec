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
import android.widget.Toast;



import com.bumptech.glide.Glide;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

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
            Picasso.with(context).load(dailyWageds.get(position).picture).into(holder.circleImageView,
                    PicassoPalette.with(dailyWageds.get(position).picture, holder.circleImageView)
                            .use(PicassoPalette.Profile.VIBRANT_DARK)
                            .intoBackground(holder.layout)
                            .intoTextColor(holder.details)

                            /*.use(PicassoPalette.Profile.VIBRANT)
                            .intoBackground(holder.cardView, PicassoPalette.Swatch.RGB)
                            .intoTextColor(holder.details, PicassoPalette.Swatch.BODY_TEXT_COLOR)*/
            );

        }
        holder.details.setText(dailyWageds.get(position).name);


    }

    @Override
    public int getItemCount() {
        return dailyWageds.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView circleImageView;
        public TextView details;
        private LinearLayout layout;

        public MyViewHolder(View view) {
            super(view);
            layout=(LinearLayout)view.findViewById(R.id.layout);
            circleImageView=(CircleImageView) view.findViewById(R.id.profile_image_add);
            details=(TextView)view.findViewById(R.id.worker_details);
        }

    }
}
