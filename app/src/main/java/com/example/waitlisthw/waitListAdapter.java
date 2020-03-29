package com.example.waitlisthw;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class waitListAdapter extends RecyclerView.Adapter<waitListAdapter.MyViewHolder>{
    private Context context;
    private List<waitList> wait_list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView student;
        public TextView priority;
        public TextView studentID;
        public TextView email;
        public TextView timestamp;

        public MyViewHolder(View view) {
            super(view);
            student = view.findViewById(R.id.student);
            priority = view.findViewById(R.id.priority);
            studentID = view.findViewById(R.id.studentID);
            email = view.findViewById(R.id.email);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }


    public waitListAdapter(Context context, List<waitList> waitingList) {
        this.context = context;
        this.wait_list = waitingList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.entry_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) { // Displays data at a specified position
        waitList entry = wait_list.get(position);

        holder.student.setText(entry.getStudent());
        holder.priority.setText(entry.getPriority());
        holder.studentID.setText(entry.getStudentID());
        holder.email.setText(entry.getEmail());

        holder.timestamp.setText(formatDate(entry.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return wait_list.size();
    }

    // formats date to day month year
    private String formatDate(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateEntered = format.parse(date);
            SimpleDateFormat printFormat = new SimpleDateFormat("dd MMM yyyy");
            return printFormat.format(dateEntered);
        } catch (ParseException e) {

        }

        return "";
    }
}