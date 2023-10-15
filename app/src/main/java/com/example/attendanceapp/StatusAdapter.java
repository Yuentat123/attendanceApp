package com.example.attendanceapp;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.MyViewHolder> {

    Context context;
//  Boolean checkstatus;
    ArrayList<StatusModel> statusModelArrayList;

    public StatusAdapter(Context context, ArrayList<StatusModel> statusModelArrayList){
        this.context = context;
        this.statusModelArrayList = statusModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_recycler, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusAdapter.MyViewHolder holder, int position) {
        StatusModel model = statusModelArrayList.get(position);
        holder.date.setText(model.getDate());
        holder.time.setText(model.getTime());
        holder.reason.setText(model.getReason());
        String value = model.getStatus().toString();

        if(value == "true"){
            holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icons8_approval_48, 0, 0, 0);
        }else{
            holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icons8_cancel_48, 0, 0, 0);
        }
    }

    @Override
    public int getItemCount() {
        return statusModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView date, time, reason;
        TextView status;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            date = itemView.findViewById(R.id.status_date);
            time = itemView.findViewById(R.id.status_time);
            reason = itemView.findViewById(R.id.status_reason);
            status = itemView.findViewById(R.id.status_img);
        }
    }
}
