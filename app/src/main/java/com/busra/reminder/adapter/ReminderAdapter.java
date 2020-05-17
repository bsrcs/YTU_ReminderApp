package com.busra.reminder.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.busra.reminder.activity.TaskEditorActivity;
import com.busra.reminder.model.Task;

import com.busra.reminder.R;

import java.util.ArrayList;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Task> tasks;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTask, descTask, dateTask;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTask = (TextView) itemView.findViewById(R.id.titleTask);
            descTask = (TextView) itemView.findViewById(R.id.descTask);
            dateTask = (TextView) itemView.findViewById(R.id.dateTask);
        }
    }

    public ReminderAdapter(Context context, ArrayList<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.titleTask.setText(tasks.get(position).getTitleTask());
        holder.descTask.setText(tasks.get(position).getDescTask());
        holder.dateTask.setText(tasks.get(position).getDateTask());

        final String title = tasks.get(position).getTitleTask();
        final String description = tasks.get(position).getDescTask();
        final String date = tasks.get(position).getDateTask();
        final String key = tasks.get(position).getKeyTask();
        final String keyFirebase = tasks.get(position).getKeyFirebase();

        //Edit Task config
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TaskEditorActivity.class);
                intent.putExtra("titleTask", title);
                intent.putExtra("descTask", description);
                intent.putExtra("dateTask", date);
                intent.putExtra("keyTask", key);
                intent.putExtra("keyFirebase", keyFirebase);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
