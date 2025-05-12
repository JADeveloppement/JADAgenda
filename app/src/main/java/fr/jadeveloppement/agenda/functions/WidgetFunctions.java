package fr.jadeveloppement.agenda.functions;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import fr.jadeveloppement.agenda.R;
import fr.jadeveloppement.agenda.ui.widget.JADAgendaWidget;

public class WidgetFunctions {

    public static void refreshWidget(Context c) {
        Context context = c;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, JADAgendaWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        if (appWidgetIds != null && appWidgetIds.length > 0) {
            Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            context.sendBroadcast(updateIntent);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetListOfTask);
        }
    }
}
