package fr.jadeveloppement.jadcalendar.components;

import static java.lang.Integer.parseInt;
import static java.util.Objects.isNull;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import fr.jadeveloppement.jadcalendar.Variables;
import fr.jadeveloppement.jadcustomcalendar.R;

public class MonthComponent {
    public interface MonthLayoutClicked{
        void prevMonthClicked();
        void nextMonthClicked();
    }

    private final String TAG = "JADCustomCalendar";
    private final Context context;
    private View monthLayout;
    private String monthDate;

    private String[] monthNamesLong = Variables.monthesNamesLong;

    private MonthLayoutClicked listener = null;

    private final Throwable
            monthDateFormatException = new IllegalArgumentException("CustomCalendar > MonthComponent > initMonthLayout: Date format error"),
            monthNamesFormatException = new IllegalArgumentException("CustomCalendar > MonthComponent > setMonthNamesLong: Argument exception error");


    public MonthComponent(@NonNull Context c){
        this.context = c;
    }

    public MonthComponent(@NonNull Context c, @NonNull String mDate, @NonNull ViewGroup parent){
        this.context = c;
        this.monthLayout = LayoutInflater.from(context).inflate(R.layout.layout_month, parent, false);
        this.monthDate = mDate;

        initMonthLayout();
    }

    public MonthComponent(@NonNull Context c, @NonNull String mDate, @NonNull ViewGroup parent, MonthLayoutClicked l){
        this.context = c;
        this.monthLayout = LayoutInflater.from(context).inflate(R.layout.layout_month, parent, false);
        this.monthDate = mDate;
        this.listener = l;

        initMonthLayout();
        initMonthEvents();
    }

    private LinearLayout nextMonthLayout, prevMonthLayout;
    private TextView monthTv;

    private void initMonthLayout() {
        nextMonthLayout = monthLayout.findViewById(R.id.layoutMonthNextMonth);
        prevMonthLayout = monthLayout.findViewById(R.id.layoutMonthPrevMonth);

        monthTv = monthLayout.findViewById(R.id.layoutMonthMonthNameTv);

        if (!monthDate.contains("-"))
            Log.e(TAG, "Should expect YYYY-MM, got " + monthDate + " instead", monthDateFormatException);

        setMonthName();
    }

    private void initMonthEvents() {
        nextMonthLayout.setOnClickListener(v -> {
            if (!isNull(listener)) listener.nextMonthClicked();
            else Log.d(TAG, "JADCustomCalendar > MonthComponent > initMonthEvent: Button next month clicked but no listener attached to it.");
        });

        prevMonthLayout.setOnClickListener(v -> {
            if (!isNull(listener)) listener.prevMonthClicked();
            else Log.d(TAG, "JADCustomCalendar > MonthComponent > initMonthEvent: Button prev month clicked but no listener attached to it.");
        });
    }

    private void setMonthName() {
        String month = monthDate.split("-")[1];
        String year = monthDate.split("-")[0];
        String monthText = monthNamesLong[parseInt(month)-1] + " " + year;

        monthTv.setText(monthText);
    }

    public void setMonthNamesLong(@NonNull String[] monthNames){
        if (monthNames.length != 12)
            Log.e(TAG, "Month array should contain 12 elements, got " + monthNames.length + " instead", monthNamesFormatException);

        this.monthNamesLong = monthNames;
        setMonthName();
    }

    public void updateMonthName(@NonNull String monthD){
        if (!monthDate.contains("-"))
            Log.e(TAG, "Should expect YYYY-MM, got " + monthDate + " instead", monthDateFormatException);

        this.monthDate = monthD;
        setMonthName();
    }

    public LinearLayout getNextMonthLayout(){
        return nextMonthLayout;
    }

    public LinearLayout getPrevMonthLayout(){
        return prevMonthLayout;
    }

    public TextView getMonthTv(){
        return monthTv;
    }

    public View getLayout(){
        return monthLayout;
    }
}