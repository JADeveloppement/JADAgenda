package fr.jadeveloppement.jadcalendar.components;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.util.List;

import fr.jadeveloppement.jadcustomcalendar.R;

public class WeekComponent {

    private final Context context;
    private View weekLayout;
    private List<DayComponent> dayComponentList;

    public WeekComponent(@NonNull Context c){
        this.context = c;
    }

    public WeekComponent(@NonNull Context c, @NonNull List<DayComponent> dayList, @NonNull ViewGroup parent){
        this.context = c;
        this.dayComponentList = dayList;
        this.weekLayout = LayoutInflater.from(context).inflate(R.layout.layout_calendar_week, parent, false);

        initWeekComponent();
    }

    private void initWeekComponent() {
        ((LinearLayout) weekLayout).setWeightSum(7);
        for (DayComponent dayC : dayComponentList){
            ((LinearLayout) weekLayout).addView(dayC.getLayout());
        }
    }

    public View getLayout(){
        return weekLayout;
    }

}
