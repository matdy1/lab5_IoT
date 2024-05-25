package com.example.lab5;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab5.entity.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private Context context;

    public TaskAdapter(List<Task> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.textViewTitle.setText(task.getTitle());
        holder.textViewDescription.setText(task.getDescription());
        holder.textViewDueDate.setText(task.getDueDate().toString());

        switch (task.getImportance()) {
            case Task.IMPORTANCE_HIGH:
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.high_priority));
                break;
            case Task.IMPORTANCE_DEFAULT:
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.default_priority));
                break;
            case Task.IMPORTANCE_LOW:
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.low_priority));
                break;
        }

        holder.buttonEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditTaskActivity.class);
            intent.putExtra("task", task);
            intent.putExtra("taskIndex", position);
            ((MainActivity) context).startActivityForResult(intent, MainActivity.EDIT_TASK_REQUEST);
        });

        // Configurar el botón de eliminación
        holder.buttonDelete.setOnClickListener(v -> {
            taskList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, taskList.size());
            ((MainActivity) context).saveTasks(taskList);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription, textViewDueDate;
        Button buttonEdit, buttonDelete;
        CardView cardView;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewDueDate = itemView.findViewById(R.id.textViewDueDate);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}

