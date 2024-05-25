package com.example.lab5;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lab5.entity.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_TASK_REQUEST = 1;
    public static final int EDIT_TASK_REQUEST = 2;
    private static final String FILE_NAME = "tasks.dat";

    private ArrayList<Task> taskList;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Inicializar el task y al adapter
        taskList = loadTasks();
        taskAdapter = new TaskAdapter(taskList, this);

        // Iniciar el  RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);

        // FloatingActionButton para agregar nuevas tareas
        FloatingActionButton fabAddTask = findViewById(R.id.fabAddTask);
        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            startActivityForResult(intent, ADD_TASK_REQUEST);
        });
    }

    private void editTask(Task task, int position) {
        Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
        intent.putExtra("task", task);
        intent.putExtra("taskIndex", position);
        startActivityForResult(intent, EDIT_TASK_REQUEST);
    }

    private void deleteTask(Task task, int position) {
        taskList.remove(position);
        taskAdapter.notifyItemRemoved(position);
        saveTasks(taskList);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_TASK_REQUEST && resultCode == RESULT_OK) {
            Task newTask = (Task) data.getSerializableExtra("task");
            taskList.add(newTask);
            taskAdapter.notifyItemInserted(taskList.size() - 1);
            saveTasks(taskList);
        } else if (requestCode == EDIT_TASK_REQUEST && resultCode == RESULT_OK) {
            Task updatedTask = (Task) data.getSerializableExtra("task");
            int taskIndex = data.getIntExtra("taskIndex", -1);
            if (taskIndex != -1) {
                taskList.set(taskIndex, updatedTask);
                taskAdapter.notifyItemChanged(taskIndex);
                saveTasks(taskList);
            }
        }
    }

    void saveTasks(List<Task> tasks) {
        try (FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(tasks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        try (FileInputStream fis = openFileInput(FILE_NAME);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            tasks = (ArrayList<Task>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }
}
