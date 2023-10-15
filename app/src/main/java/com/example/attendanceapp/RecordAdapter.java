package com.example.attendanceapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.MyViewHolder>{

    Context context;
    ArrayList<RecordModel> recordModelArrayList;

    public RecordAdapter(Context context, ArrayList<RecordModel> recordModelArrayList){
        this.context = context;
        this.recordModelArrayList = recordModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_record_recycler, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordAdapter.MyViewHolder holder, int position) {
        RecordModel model = recordModelArrayList.get(position);

        holder.courseCode.setText(model.getCourseCode());
        holder.type.setText(model.getType());
        holder.date.setText(model.getRecord_Date());
        String status = model.getRecord_Status().toString();

        if(status == "true"){
            holder.status.setText("Attended");
        }else{
            holder.status.setText("Absent");
        }
    }

    @Override
    public int getItemCount() {
        return recordModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView courseCode, type, date;
        TextView status;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            courseCode = itemView.findViewById(R.id.course_record);
            type = itemView.findViewById(R.id.type_record);
            date = itemView.findViewById(R.id.date_record);
            status= itemView.findViewById(R.id.status_record);
        }
    }
}
