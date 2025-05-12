package fr.jadeveloppement.agenda.functions;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import fr.jadeveloppement.agenda.R;

public class NotificationHelper {

    public static final String CHANNEL_ID = "JAD Agenda Notification";
    private final NotificationManager notificationManager;
    private final Context context;

    public NotificationHelper(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "JADAgenda", NotificationManager.IMPORTANCE_DEFAULT));
    }

    public void createNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp) // Replace with your notification icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true); // Dismiss the notification when the user clicks it

        notificationManager.notify(1, builder.build()); // Use a unique ID for each notification
    }
}
