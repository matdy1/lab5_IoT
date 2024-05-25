package com.example.lab5;


import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lab5.entity.Task;
import java.util.Calendar;

public class AddEditTaskActivity extends AppCompatActivity {

    private static final int NOTIFICATION_ID_TASK_COUNT = 1;
    private static final int NOTIFICATION_ID_USER = 2;
    private static final String CHANNEL_ID_HIGH = "HIGH_IMPORTANCE_CHANNEL";
    private static final String CHANNEL_ID_DEFAULT = "DEFAULT_IMPORTANCE_CHANNEL";
    private static final String CHANNEL_ID_LOW = "LOW_IMPORTANCE_CHANNEL";
    private EditText editTextTitle;
    private EditText editTextDescription;
    private TextView textViewDateTime;
    private Spinner spinnerImportance;
    private Button buttonSave;
    private int contador;
    private Calendar dateTime;
    private boolean isEditing = false;
    private int taskIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        textViewDateTime = findViewById(R.id.textViewDateTime);
        spinnerImportance= findViewById(R.id.spinnerImportance);
        buttonSave = findViewById(R.id.buttonSave);
        dateTime = Calendar.getInstance();

        String codigo = getIntent().getStringExtra("codigo");

        Intent intent = getIntent();
        if (intent.hasExtra("task")) {
            Task task = (Task) intent.getSerializableExtra("task");
            taskIndex = intent.getIntExtra("taskIndex", -1);
            isEditing = true;
            populateTaskDetails(task);
        }

        textViewDateTime.setOnClickListener(v -> showDateTimePicker());

        buttonSave.setOnClickListener(v -> {
            if (isInputValid()) {
                saveTask(codigo);
            }
        });
    }

    private void populateTaskDetails(Task task) {
        editTextTitle.setText(task.getTitle());
        editTextDescription.setText(task.getDescription());
        dateTime.setTime(task.getDueDate());
        textViewDateTime.setText(task.getDueDate().toString());

        switch (task.getImportance()) {
            case Task.IMPORTANCE_LOW:
                spinnerImportance.setSelection(0);
                break;
            case Task.IMPORTANCE_DEFAULT:
                spinnerImportance.setSelection(1);
                break;
            case Task.IMPORTANCE_HIGH:
                spinnerImportance.setSelection(2);
                break;
        }
    }

    private void showDateTimePicker() {
        Calendar currentDateTime = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, month);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                dateTime.set(Calendar.MINUTE, minute);
                textViewDateTime.setText(dateTime.getTime().toString());
            }, currentDateTime.get(Calendar.HOUR_OF_DAY), currentDateTime.get(Calendar.MINUTE), true).show();
        }, currentDateTime.get(Calendar.YEAR), currentDateTime.get(Calendar.MONTH), currentDateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    private boolean isInputValid() {
        if (editTextTitle.getText().toString().trim().isEmpty() || editTextDescription.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Título y Descripción no pueden estar vacíos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveTask(String codigo) {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        int importance = Task.IMPORTANCE_DEFAULT;

        switch (spinnerImportance.getSelectedItemPosition()) {
            case 0:
                importance = Task.IMPORTANCE_LOW;
                break;
            case 1:
                importance = Task.IMPORTANCE_DEFAULT;
                break;
            case 2:
                importance = Task.IMPORTANCE_HIGH;
                break;
        }

        

        Task task = new Task(title, description, dateTime.getTime(), importance);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("task", task);
        if (isEditing) {
            resultIntent.putExtra("taskIndex", taskIndex);
        }
        setResult(RESULT_OK, resultIntent);
        finish();
        contador=contador+1;
        showNotification(task);
        updatePersistentNotification(codigo,contador);

    }

    private boolean isTaskWithinNextThreeHours(Calendar taskTime) {
        Calendar now = Calendar.getInstance();
        Calendar threeHoursLater = Calendar.getInstance();
        threeHoursLater.add(Calendar.HOUR_OF_DAY, 3);

        return taskTime.after(now) && taskTime.before(threeHoursLater);
    }

    private void showNotification(Task task) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId;
        int priority;
        switch (task.getImportance()) {
            case Task.IMPORTANCE_HIGH:
                channelId = CHANNEL_ID_HIGH;
                priority = NotificationCompat.PRIORITY_HIGH;
                break;
            case Task.IMPORTANCE_DEFAULT:
                channelId = CHANNEL_ID_DEFAULT;
                priority = NotificationCompat.PRIORITY_DEFAULT;
                break;
            case Task.IMPORTANCE_LOW:
                channelId = CHANNEL_ID_LOW;
                priority = NotificationCompat.PRIORITY_LOW;
                break;
            default:
                channelId = CHANNEL_ID_DEFAULT;
                priority = NotificationCompat.PRIORITY_DEFAULT;
                break;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Nueva tarea")
                .setContentText(task.getTitle())
                .setPriority(priority)
                .setAutoCancel(true);

        notificationManager.notify(task.hashCode(), builder.build());
    }

    private void updatePersistentNotification(String codigo,int contador) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int taskCount =contador;

        NotificationCompat.Builder taskCountNotificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID_LOW)
                .setContentTitle("Tareas en curso")
                .setContentText("Tienes " + taskCount + " tareas en curso")
                .setSmallIcon(R.drawable.ic_notification)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        NotificationCompat.Builder userNotificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID_LOW)
                .setContentTitle("Usuario logueado")
                .setContentText("Usuario: " + codigo)
                .setSmallIcon(R.drawable.ic_notification)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        notificationManager.notify(NOTIFICATION_ID_TASK_COUNT, taskCountNotificationBuilder.build());
        notificationManager.notify(NOTIFICATION_ID_USER, userNotificationBuilder.build());
    }



}
