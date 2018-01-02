package com.nearur.payrec;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mrdis on 12/31/2017.
 */

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.Myview> {

    ArrayList<dailyWork> dailyWorks;
    String type;
    ArrayList<Attendance>attendances;
    ArrayList<AdvancePay>advancePays;
    ArrayList<Payments>payments;
    Context context;
    boolean advance,pay;

    public void setAdvancePays(ArrayList<AdvancePay> advancePays) {
        this.advancePays = advancePays;
    }

    public void setPayments(ArrayList<Payments> payments) {
        this.payments = payments;
    }

    public DetailsAdapter(ArrayList<dailyWork> dailyWorks, String type, ArrayList<Attendance> attendances, boolean advance) {
        this.dailyWorks = dailyWorks;
        this.type=type;
        this.attendances=attendances;
        this.advance=advance;
    }

    @Override
    public Myview onCreateViewHolder(ViewGroup parent, int viewType) {
        context=parent.getContext();
        return new Myview(LayoutInflater.from(parent.getContext()).inflate(R.layout.due_details,parent,false));
    }

    @Override
    public void onBindViewHolder(Myview holder, int position) {

        if(advance){

            holder.date.setText(advancePays.get(position).date.toString());
            holder.total.setText(advancePays.get(position).amount+"");
            holder.comment.setText("Approved By : "+advancePays.get(position).approver.toString());
            holder.details.setVisibility(View.GONE);
        }
        else if (pay){
            holder.date.setText(payments.get(position).date.toString());
            holder.total.setText(payments.get(position).amount_paid+"");
            holder.comment.setText("Approved By : "+payments.get(position).approver.toString());
            holder.details.setVisibility(View.GONE);
        }else{

        if(type.equals("Salaried")){
            holder.date.setText(attendances.get(position).date.toString());
            holder.total.setCompoundDrawables(null,null,null,null);
            holder.details.setVisibility(View.GONE);

            holder.total.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            holder.comment.setText("Approved By : "+attendances.get(position).approver.toString());
            if(attendances.get(position).present){
                holder.total.setText("Present");
            }else{
                holder.total.setText("Absent");
            }


        }else{
            holder.date.setText(dailyWorks.get(position).date.toString());
            holder.total.setText(dailyWorks.get(position).total+"");
            holder.comment.setText(dailyWorks.get(position).approver.toString());
            holder.details.setText("Quantity : "+dailyWorks.get(position).units+"   Price : ₹ "+dailyWorks.get(position).price_per_unit);

        }
        }

    }

    @Override
    public int getItemCount() {
        if (pay){
            return payments.size();
        }

        if (!advance && !pay){
            if(type.equals("Salaried")){
                return attendances.size();
            }
            else{
                return dailyWorks.size();
            }
        }

        return advancePays.size();
    }

    public class Myview extends RecyclerView.ViewHolder{

        private CheckedTextView date;
        private TextView total,comment,details;

        public Myview(View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.date);
            total =itemView.findViewById(R.id.total);
            comment=itemView.findViewById(R.id.comment);
            details=itemView.findViewById(R.id.details);
        }
    }

}
