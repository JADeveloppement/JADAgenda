package fr.jadeveloppement.agenda;

import static fr.jadeveloppement.agenda.functions.NotificationHelper.CHANNEL_ID;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.AlarmManagerCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import fr.jadeveloppement.agenda.databinding.ActivityMainBinding;
import fr.jadeveloppement.agenda.functions.NotificationHelper;
import fr.jadeveloppement.agenda.functions.broadcast.ReminderBroadcastReceiver;

public class MainActivity extends AppCompatActivity {

    private static ActivityMainBinding binding;

    private ActivityResultLauncher<Intent> overlayPermissionResultLauncher;

    public static int getNavViewHeight() {
        return navView.getHeight();
    }

    private static BottomNavigationView navView;

    private static final String TAG = "AlarmPermission";
    private ActivityResultLauncher<Intent> alarmPermissionResultLauncher;
    private AlarmManager alarmManager;

    public static Context getContext() {
        return getContext().getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

//        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

//        setMyExactAlarm();
    }

    private void setMyExactAlarm() {
        // Now you have the AlarmManager instance
        // Example of setting an alarm (replace with your actual alarm logic):
        Intent alarmIntent = new Intent(this, ReminderBroadcastReceiver.class); // You'll need an AlarmReceiver class
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE); // Use FLAG_IMMUTABLE or FLAG_MUTABLE

        long triggerTime = System.currentTimeMillis() + 5000; // Example: 5 seconds from now

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (AlarmManagerCompat.canScheduleExactAlarms(alarmManager)) {
                    AlarmManagerCompat.setExactAndAllowWhileIdle((AlarmManager) getSystemService(Context.ALARM_SERVICE), AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                    Toast.makeText(this, "Alarm set for 5 seconds from now", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager, AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                Toast.makeText(this, "Alarm set for 5 seconds from now", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Log.e(TAG, "AlarmManager is null.  Cannot set alarm.");
        }
    }

    public static ConstraintLayout getMainView(){
        return binding.getRoot();
    }

}