
package com.example.lab5;
import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.lab5.R;
import com.example.lab5.entity.Task;

import java.util.Calendar;

public class AddEditTaskActivity extends AppCompatActivity {

    private static final int REQUEST_SCHEDULE_EXACT_ALARM = 1;

    private EditText editTextTitle;
    private EditText editTextDescription;
    private TextView textViewDateTime;
    private Spinner spinnerImportance;
    private Button buttonSave;
    private Calendar dateTime;
    private boolean isEditing = false;
    private int taskIndex = -1;
    private String userCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        textViewDateTime = findViewById(R.id.textViewDateTime);
        spinnerImportance = findViewById(R.id.spinnerImportance);
        buttonSave = findViewById(R.id.buttonSave);
        dateTime = Calendar.getInstance();

        Intent intent = getIntent();
        userCode = intent.getStringExtra("codigo");

        if (intent.hasExtra("task")) {
            Task task = (Task) intent.getSerializableExtra("task");
            taskIndex = intent.getIntExtra("taskIndex", -1);
            isEditing = true;
            populateTaskDetails(task);
        }

        textViewDateTime.setOnClickListener(v -> showDateTimePicker());

        buttonSave.setOnClickListener(v -> {
            if (isInputValid()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmPermissionGranted()) {
                    requestAlarmPermission();
                } else {
                    saveTask();
                }
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

    private void saveTask() {
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

        if (importance == Task.IMPORTANCE_HIGH) {
            scheduleHighPriorityTaskNotification(task);
        } else if (importance == Task.IMPORTANCE_DEFAULT) {
            scheduleDefaultPriorityTaskNotification(task);
        } else if (importance == Task.IMPORTANCE_LOW) {
            scheduleLowPriorityTaskNotification(task);
        }
    }

    private void scheduleHighPriorityTaskNotification(Task task) {
        Intent notificationIntent = new Intent(this, TaskNotificationReceiver.class);
        notificationIntent.putExtra("taskTitle", task.getTitle());
        notificationIntent.putExtra("taskDescription", task.getDescription());
        notificationIntent.putExtra("taskImportance", Task.IMPORTANCE_HIGH);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(),
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, task.getDueDate().getTime(), pendingIntent);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, task.getDueDate().getTime(), pendingIntent);
        } else {
            Toast.makeText(this, "No se puede programar una alarma exacta, falta el permiso", Toast.LENGTH_SHORT).show();
        }
    }

    private void scheduleDefaultPriorityTaskNotification(Task task) {
        Intent notificationIntent = new Intent(this, TaskNotificationReceiver.class);
        notificationIntent.putExtra("taskTitle", task.getTitle());
        notificationIntent.putExtra("taskDescription", task.getDescription());
        notificationIntent.putExtra("taskImportance", Task.IMPORTANCE_DEFAULT);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(),
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, task.getDueDate().getTime(), pendingIntent);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, task.getDueDate().getTime(), pendingIntent);
        } else {
            Toast.makeText(this, "No se puede programar una alarma exacta, falta el permiso", Toast.LENGTH_SHORT).show();
        }
    }

    private void scheduleLowPriorityTaskNotification(Task task) {
        Intent notificationIntent = new Intent(this, TaskNotificationReceiver.class);
        notificationIntent.putExtra("taskTitle", task.getTitle());
        notificationIntent.putExtra("taskDescription", task.getDescription());
        notificationIntent.putExtra("taskImportance", Task.IMPORTANCE_LOW);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(),
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, task.getDueDate().getTime(), pendingIntent);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, task.getDueDate().getTime(), pendingIntent);
        } else {
            Toast.makeText(this, "No se puede programar una alarma exacta, falta el permiso", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean alarmPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM}, REQUEST_SCHEDULE_EXACT_ALARM);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SCHEDULE_EXACT_ALARM) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveTask();
            } else {
                Toast.makeText(this, "Permiso de alarma exacta denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
