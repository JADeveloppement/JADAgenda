package fr.jadeveloppement.agenda.ui.dashboard;

import static java.util.Objects.isNull;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.concurrent.atomic.AtomicInteger;

import fr.jadeveloppement.agenda.MainActivity;
//import fr.jadeveloppement.agenda.components.CustomCalendar;
import fr.jadeveloppement.agenda.components.DayComponent;
import fr.jadeveloppement.agenda.components.animation.SlideAnimation;
import fr.jadeveloppement.agenda.databinding.FragmentDashboardBinding;

import fr.jadeveloppement.agenda.functions.Functions;
import fr.jadeveloppement.agenda.functions.Variables;
import fr.jadeveloppement.jadcustomcalendar.CustomCalendar;

public class DashboardFragment extends Fragment implements CustomCalendar.DateChanged {

    private String TAG = "agenda";

    private FragmentDashboardBinding binding;
    private LinearLayout dashboardAgendaContainer,
            dashboardPrevWeek,
            dashboardNextWeek,
            dashboardWeekLayoutContainer,
            dashboardDaysLayoutContainer;

    private AtomicInteger agendaCalendarInitialHeight;

    private TextView dashboardCurrentWeek,
            dashboardWeekRange;

    private ScrollView dashboardScrollView;

//    private CustomCalendar calendar;

    private int currentWeek;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        currentWeek = -2;
        agendaCalendarInitialHeight = new AtomicInteger(-2);

        View root = binding.getRoot();

        dashboardAgendaContainer = binding.dashboardAgendaContainer;
        dashboardWeekLayoutContainer = binding.dashboardWeekLayoutContainer;
        dashboardPrevWeek = binding.dashboardPrevWeek;
        dashboardNextWeek = binding.dashboardNextWeek;
        dashboardDaysLayoutContainer = binding.dashboardDaysLayoutContainer;

        dashboardCurrentWeek = binding.dashboardCurrentWeek;
        dashboardWeekRange = binding.dashboardWeekRange;

        dashboardScrollView = binding.dashboardScrollView;

        setCalendarView();

        dashboardWeekLayoutContainer.setOnClickListener(v -> {
            if (agendaCalendarInitialHeight.get() > 10){
                if (dashboardAgendaContainer.getHeight() > 100){
                    new SlideAnimation(getContext()).slideUp(dashboardAgendaContainer);
                } else {
                    new SlideAnimation(getContext()).slideDown(dashboardAgendaContainer, agendaCalendarInitialHeight.get());
                }
            }
        });

        dashboardScrollView.post(() -> dashboardScrollView.setPadding(
                dashboardScrollView.getPaddingLeft(),
                dashboardScrollView.getPaddingTop(),
                dashboardScrollView.getPaddingRight(),
                MainActivity.getNavViewHeight() + 16
        ));

        return root;
    }

    private CustomCalendar customCalendar;

    private void setCalendarView(){

        customCalendar = new CustomCalendar(getContext(), this);

        dashboardAgendaContainer.addView(customCalendar.getCustomCalendarLayout());
        dashboardAgendaContainer.post(() -> agendaCalendarInitialHeight.set(dashboardAgendaContainer.getHeight()));

        dashboardPrevWeek.setOnClickListener(v -> {
            customCalendar.addInterval(-1, "week");
        });

        dashboardNextWeek.setOnClickListener(v -> {
            customCalendar.addInterval(1, "week");
        });

        updateTasksUI();
    }

    private void updateTasksUI(){
        String[] weekRange = customCalendar.getWeekRange();
        String weekRangeLeft = Functions.convertStdDateToLocale(weekRange[0]);
        String weekRangeRight = Functions.convertStdDateToLocale(weekRange[1]);
        String weekRangeTvTxt = weekRangeLeft + " - " + weekRangeRight;
        dashboardWeekRange.setText(weekRangeTvTxt);
        dashboardCurrentWeek.setText("Semaine  " + customCalendar.getWeekNumber());

        if (currentWeek != customCalendar.getWeekNumber()) {
            dashboardDaysLayoutContainer.removeAllViews();
            currentWeek = customCalendar.getWeekNumber();

            for (String day : customCalendar.getListOfDatesOfWeek()){
                dashboardDaysLayoutContainer.addView(new DayComponent(getContext(), Variables.days[Functions.getDayOfWeekIndex(day)], Functions.convertStdDateToLocale(day)));
            }
        }
    }

    @Override
    public void selectedDayChanged(){
        if (!isNull(customCalendar)){
            updateTasksUI();
            Log.d(TAG, "selectedDayChanged: weeknumber : " + customCalendar.getWeekNumber() + "\nweekdates : " + customCalendar.getWeekRange() + "\ngetdate : ");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: view destroyed");
        binding = null;

    }
}