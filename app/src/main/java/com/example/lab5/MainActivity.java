package com.example.lab5;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
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
    private String userCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        userCode = intent.getStringExtra("codigo");
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
            Intent addTaskIntent  = new Intent(MainActivity.this, AddEditTaskActivity.class);
            addTaskIntent.putExtra("codigo", userCode);
            startActivityForResult(addTaskIntent, ADD_TASK_REQUEST);
        });

        // Mostrar notificación persistente del usuario logueado
        updateUserNotification(userCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_TASK_REQUEST && resultCode == RESULT_OK && data != null) {
            Task newTask = (Task) data.getSerializableExtra("task");
            if (newTask != null) {
                taskList.add(newTask);
                taskAdapter.notifyItemInserted(taskList.size() - 1);
                saveTasks(taskList);
            }
        } else if (requestCode == EDIT_TASK_REQUEST && resultCode == RESULT_OK && data != null) {
            Task updatedTask = (Task) data.getSerializableExtra("task");
            int taskIndex = data.getIntExtra("taskIndex", -1);
            if (updatedTask != null && taskIndex != -1) {
                taskList.set(taskIndex, updatedTask);
                taskAdapter.notifyItemChanged(taskIndex);
                saveTasks(taskList);
            }
        }
        updateTaskCountNotification(taskList.size());
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

    private void updateTaskCountNotification(int taskCount) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id_low")
                .setContentTitle("Tareas en curso")
                .setContentText("Tienes " + taskCount + " tareas en curso")
                .setSmallIcon(R.drawable.ic_notification)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        notificationManager.notify(1, builder.build());
    }

    private void updateUserNotification(String userCode) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id_low")
                .setContentTitle("Usuario logueado")
                .setContentText("Logueado como: " + userCode)
                .setSmallIcon(R.drawable.ic_notification)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        notificationManager.notify(2, builder.build());
    }
}
