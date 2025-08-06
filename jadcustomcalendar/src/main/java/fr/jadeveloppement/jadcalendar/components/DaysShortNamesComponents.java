package fr.jadeveloppement.jadcalendar.components;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import fr.jadeveloppement.jadcalendar.Variables;
import fr.jadeveloppement.jadcustomcalendar.R;

public class DaysShortNamesComponents {
    private final String TAG = "JADCustomCalendar";

    private final Context context;
    private View daysShortLayoutContainer;
    private View daysShortNamesLayout;

    private String[] daysShort = Variables.daysShortNames;

    public DaysShortNamesComponents(@NonNull Context c){
        this.context = c;
    }

    public DaysShortNamesComponents(@NonNull Context c, @NonNull ViewGroup parent){
        this.context = c;
        this.daysShortNamesLayout = LayoutInflater.from(context).inflate(R.layout.layout_days_short, parent, false);
        this.daysShortLayoutContainer = daysShortNamesLayout.findViewById(R.id.layoutCustomCalendarDayShortContainer);
        initLayout();
    }

    public void setDaysShortNames(@NonNull String[] dNames){
        if (dNames.length != 7) Log.e(TAG, "JADCustomCalendar > DaysShortNamesComponent > setDaysShortNames", new IllegalArgumentException("Expected an array of 7 got " + dNames.length + " instead."));
        else this.daysShort = dNames;

        initLayout();
    }

    private void initLayout() {
        ((LinearLayout) daysShortLayoutContainer).removeAllViews();
        for (String dayS : daysShort){
            View dayShortLayout = LayoutInflater.from(context).inflate(R.layout.layout_day_short_element, (ViewGroup) daysShortNamesLayout, false);
            LinearLayout.LayoutParams dayShortLayoutParams = new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1
            );
            dayShortLayout.setLayoutParams(dayShortLayoutParams);
            TextView dayTv = dayShortLayout.findViewById(R.id.layoutCustomCalendarDayShortTv);
            dayTv.setText(dayS);

            ((LinearLayout) daysShortLayoutContainer).addView(dayShortLayout);
        }
    }

    public View getLayout() {
        return daysShortNamesLayout;
    }
}
