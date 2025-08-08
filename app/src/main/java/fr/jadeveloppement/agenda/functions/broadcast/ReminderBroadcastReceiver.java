package fr.jadeveloppement.agenda.functions.broadcast;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import fr.jadeveloppement.agenda.MainActivity;
import fr.jadeveloppement.agenda.R;
import fr.jadeveloppement.agenda.functions.NotificationHelper;

public class ReminderBroadcastReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "JADAgenda";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "JADAgenda", NotificationManager.IMPORTANCE_HIGH);
        manager.createNotificationChannel(channel);

        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivities(context, 0, new Intent[]{activityIntent}, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Rappel Tâche")
                .setContentText(intent.getStringExtra("message"))
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        manager.notify(intent.getIntExtra("notificationID", 0), builder.build());
    }
}