package com.example.lab5;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

import com.example.lab5.entity.Task;

public class TaskNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String taskTitle = intent.getStringExtra("taskTitle");
        String taskDescription = intent.getStringExtra("taskDescription");
        int taskImportance = intent.getIntExtra("taskImportance", Task.IMPORTANCE_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId;
        int priority;

        switch (taskImportance) {
            case Task.IMPORTANCE_HIGH:
                channelId = "channel_id_high";
                priority = NotificationCompat.PRIORITY_HIGH;
                break;
            case Task.IMPORTANCE_DEFAULT:
                channelId = "channel_id_default";
                priority = NotificationCompat.PRIORITY_DEFAULT;
                break;
            case Task.IMPORTANCE_LOW:
                channelId = "channel_id_low";
                priority = NotificationCompat.PRIORITY_LOW;
                break;
            default:
                channelId = "channel_id_default";
                priority = NotificationCompat.PRIORITY_DEFAULT;
                break;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(taskTitle)
                .setContentText(taskDescription)
                .setPriority(priority)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
