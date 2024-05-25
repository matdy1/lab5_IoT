package com.example.lab5;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class IdentificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_identification);
        EditText codigo = findViewById(R.id.codigo);
        Button ingreso = findViewById(R.id.ingresar);
        ingreso.setOnClickListener(v -> {
            Intent intent = new Intent(IdentificationActivity.this, MainActivity.class);
            intent.putExtra("codigo", codigo.getText().toString());
            startActivity(intent);
        });

        createNotificationChannels();
        askNotificationPermission();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel highImportanceChannel = new NotificationChannel(
                    "HIGH_IMPORTANCE_CHANNEL",
                    "High Importance Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            highImportanceChannel.setDescription("This is for high importance notifications");

            NotificationChannel defaultImportanceChannel = new NotificationChannel(
                    "DEFAULT_IMPORTANCE_CHANNEL",
                    "Default Importance Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            defaultImportanceChannel.setDescription("This is for default importance notifications");

            NotificationChannel lowImportanceChannel = new NotificationChannel(
                    "LOW_IMPORTANCE_CHANNEL",
                    "Low Importance Notifications",
                    NotificationManager.IMPORTANCE_LOW
            );
            lowImportanceChannel.setDescription("This is for low importance notifications");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(highImportanceChannel);
            manager.createNotificationChannel(defaultImportanceChannel);
            manager.createNotificationChannel(lowImportanceChannel);
        }
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }



}
