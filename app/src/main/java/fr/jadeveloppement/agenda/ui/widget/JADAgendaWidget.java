package fr.jadeveloppement.agenda.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import fr.jadeveloppement.agenda.MainActivity;
import fr.jadeveloppement.agenda.R;
import fr.jadeveloppement.agenda.functions.Functions;
import fr.jadeveloppement.agenda.functions.sqlite.functions.TasksFunctionsSQL;

public class JADAgendaWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.agenda_widget_layout);

        setRemoteViewService(context, appWidgetManager, appWidgetId, views);
        setLayoutClickEvents(context, appWidgetManager, appWidgetId, views);

        views.setTextViewText(R.id.widgetAgendaDayTitle, Functions.convertStdDateToLongLocale(Functions.getTodayDate()));
        views.setTextViewText(R.id.agendaWidgetNbTask, String.valueOf((new TasksFunctionsSQL(context)).getTaskOfPeriod(Functions.getTodayDate()).size()));

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void setRemoteViewService(Context context, AppWidgetManager appWidgetManager, int appWidgetId, RemoteViews views) {
        Intent serviceIntent = new Intent(context, WidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.widgetListOfTask, serviceIntent);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widgetListOfTask);
    }

    private void setLayoutClickEvents(Context context, AppWidgetManager appWidgetManager, int appWidgetId, RemoteViews views) {
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        views.setOnClickPendingIntent(R.id.widgetAgendaTitleContainer, PendingIntent.getActivity(context, 0, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras != null){
                int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                if (appWidgetIds != null){
                    for (int appWidgetId : appWidgetIds) {
                        updateAppWidget(context, AppWidgetManager.getInstance(context), appWidgetId);
                    }
                }
            }
        }
    }
}
