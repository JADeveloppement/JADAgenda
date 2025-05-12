package fr.jadeveloppement.agenda.ui.widget;

import static java.util.Objects.isNull;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import fr.jadeveloppement.agenda.R;
import fr.jadeveloppement.agenda.functions.Functions;
import fr.jadeveloppement.agenda.functions.sqlite.functions.TasksFunctionsSQL;
import fr.jadeveloppement.agenda.functions.sqlite.tables.TasksTable;

public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private String TAG = "checklist";

    private Context mContext;
    private int mAppWidgetId;
    private List<TasksTable> mItems = new ArrayList<>();
    private final TasksFunctionsSQL taskFunctionsSQL;

    public WidgetRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        this.taskFunctionsSQL = new TasksFunctionsSQL(mContext);
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        mItems.clear();
        List<TasksTable> tasksOfDay = taskFunctionsSQL.getTaskOfPeriod(Functions.getTodayDate());
        if (tasksOfDay.isEmpty()){
            mItems.add(Functions.createMockTask());
        } else mItems.addAll(tasksOfDay);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_task_item);
        Log.d(TAG, "getViewAt: mitems size : " + mItems.size() + "position : " + position + " item : " + mItems.get(position).label);
        if (!isNull(mItems.get(position))){
            int resourceId = mItems.get(position).done == 1 ? R.layout.widget_task_item_done : R.layout.widget_task_item;
            views = new RemoteViews(mContext.getPackageName(), resourceId);
            if (mItems.get(0).task_ID != -1) {
                views.setTextViewText(R.id.taskLayoutLabelTv, mItems.get(position).label);
            }
            else {
                views.setTextViewText(R.id.taskLayoutLabelTv, "Aucune tâche à ce jour.");
            }
        }
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}