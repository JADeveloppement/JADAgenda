package fr.jadeveloppement.agenda.functions;

import static java.lang.Integer.parseInt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import fr.jadeveloppement.agenda.MainActivity;
import fr.jadeveloppement.agenda.functions.broadcast.ReminderBroadcastReceiver;
import fr.jadeveloppement.agenda.functions.sqlite.tables.TasksTable;

public class Functions {

    public static int getDaysInMonth(String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate locale = LocalDate.parse(dateString, formatter);

        return locale.lengthOfMonth();
    }

    /**
     * @param dateString : YYYY-MM-DD
     * @return : 0 to 6 (Monday to Sunday)
     */
    public static int getDayOfWeekIndex(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateString, formatter);

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.getValue() - 1; // Adjust to 0-based index
    }

    public static String getTodayDate(){
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int monthOfYear = cal.get(Calendar.MONTH)+1;
        int year = cal.get(Calendar.YEAR);

        String day = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
        String month = monthOfYear < 10 ? "0" + monthOfYear : String.valueOf(monthOfYear);

        return year + "-" + month + "-" + day;
    }

    public static double getNumberOfWeekOfMonth(int currentYear, int currentMonth) {
        String month = currentMonth < 10 ? "0" + currentMonth : String.valueOf(currentMonth);
        double nbDaysInMonth = getDaysInMonth(currentYear+"-"+month+"-01");
        return Math.ceil(nbDaysInMonth/7);
    }

    public static String convertStdDateToLocale(String date){
        String[] dateSplitted = date.split("-");
        String day = dateSplitted[2];
        String month = dateSplitted[1];
        String year = dateSplitted[0];

        return day + "/" + month + "/" + year;
    }

    public static String convertStdDateToLongLocale(String date){
        int indexDay = getDayOfWeekIndex(date);
        String[] dateSplitted = date.split("-");
        int day = parseInt(dateSplitted[2]);
        int month = parseInt(dateSplitted[1]);
        int year = parseInt(dateSplitted[0]);

        return Variables.days[indexDay] + " " + day + " " + Variables.monthes[month] + " " + year;
    }

    public static String convertLocaleDateToStd(String date){
        String[] dateSplitted = date.split("/");
        String day = dateSplitted[0];
        String month = dateSplitted[1];
        String year = dateSplitted[2];

        return year + "-" + month + "-" + day;
    }

    /**
     *
     * @param dateString YYYY-MM-DD
     * @return the weeknumber of the date
     */
    public static int getWeekNumberFromDate(String dateString) {
        try {
            Locale locale = Locale.FRANCE;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dateString, formatter);

            WeekFields weekFields = WeekFields.of(locale);
            return date.get(weekFields.weekOfYear());

        } catch (Exception e) {
            // Handle parsing errors or other exceptions
            e.printStackTrace();
            return -1; // Or throw an exception
        }
    }

    public static List<String> getTasksExamples() {
        List<String> tasks = new ArrayList<>();
        for(int i = 0; i < (new Random().nextInt(10)); i++)
            tasks.add("Tache " + i);

        return tasks;
    }

    public static int getDpInPx(Context c, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics());
    }

    /**
     * Return the UNIX timestamp of a date dd/MM/yyyy HH:mm
     * @param dateString
     * @return : UNIX timestamp
     */
    public static long getMillisecondsFromDateHourString(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date date = sdf.parse(dateString);
            if (date != null){
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                return c.getTimeInMillis();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void createReminder(Context context, String r, TasksTable task) {
        long reminderDate = getMillisecondsFromDateHourString(r);
        String notification_ID = task.notification_ID;

        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        intent.putExtra("message", task.label);
        intent.putExtra("notificationId", parseInt(notification_ID));

        Log.d("JADagenda", "createReminder: rappel créé le " + reminderDate + " id : " + parseInt(notification_ID));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, parseInt(notification_ID), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderDate, pendingIntent);
        } catch(SecurityException ex){
            Log.d("checklist", "Erreur alarm set : " + ex.getMessage());
            Toast.makeText(context, "Une erreur est survenue lors du réglage du rappel.", Toast.LENGTH_LONG).show();
        }

    }

    public static String addXDay(String dateString, int interval){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateString);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, interval);
            return sdf.format(cal.getTime());

        } catch (Exception e) {
            Log.d("JADAgenda", "addXDay: error > " + e );
            return null;
        }
    }

    public static TasksTable createMockTask() {
        TasksTable task = new TasksTable();
        task.label = "Aucune tâche à ce jour";
        task.task_ID = -1;
        task.done =  0;
        task.task_ID_parent = null;
        task.repeated = 0;
        task.repeatFrequency = "";
        task.notification_ID = "0";
        task.orderNumber = 0;
        task.date = Functions.getTodayDate();
        task.reminderDate = "";
        return task;
    }
}
