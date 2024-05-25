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
        ;

        createNotificationChannels();
        askNotificationPermission();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel highChannel = new NotificationChannel(
                    "channel_id_high",
                    "High Importance Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            highChannel.setDescription("This channel is used for high importance notifications.");

            NotificationChannel defaultChannel = new NotificationChannel(
                    "channel_id_default",
                    "Default Importance Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            defaultChannel.setDescription("This channel is used for default importance notifications.");

            NotificationChannel lowChannel = new NotificationChannel(
                    "channel_id_low",
                    "Low Importance Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            lowChannel.setDescription("This channel is used for low importance notifications.");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(highChannel);
            notificationManager.createNotificationChannel(defaultChannel);
            notificationManager.createNotificationChannel(lowChannel);
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
