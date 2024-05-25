package com.example.lab5;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.lab5.entity.Task;

import java.util.List;

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
    }
}
