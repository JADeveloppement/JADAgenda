package fr.jadeveloppement.jadcalendar.components;

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

public class DayComponent {

    private DayClicked listener;
    private View dayLayout;

    public interface DayClicked {
        void dayClicked();
    }

    private final Context context;
    private String dayDate;

    public DayComponent(@NonNull Context c){
        this.context = c;
    }

    public DayComponent(@NonNull Context c, @NonNull String dDate, @NonNull ViewGroup parent, DayClicked l){
        this.context = c;
        this.dayDate = dDate;
        this.dayLayout = LayoutInflater.from(context).inflate(R.layout.layout_calendar_day, parent, false);
        this.listener = l;

        initDay();
    }

    private TextView dayTv;
    private LinearLayout dayLayoutContainer;

    private void initDay() {
        LinearLayout.LayoutParams dayLayoutParams = new LinearLayout.LayoutParams(
                0,
                80,
                1
        );
        dayLayout.setLayoutParams(dayLayoutParams);
        dayTv = dayLayout.findViewById(R.id.layoutCustomCalendarDayTv);
        dayLayoutContainer = dayLayout.findViewById(R.id.layoutCustomCalendarDayLayout);

        dayLayout.setOnClickListener(v -> {
            if (!isNull(listener)) listener.dayClicked();
        });
    }

    public LinearLayout getLayout(){
        return (LinearLayout) dayLayout;
    }

    public LinearLayout getDayLayoutContainer(){
        return dayLayoutContainer;
    }

    public TextView getDayTv(){
        return dayTv;
    }

    public String getDayDate(){
        return dayDate;
    }
}
