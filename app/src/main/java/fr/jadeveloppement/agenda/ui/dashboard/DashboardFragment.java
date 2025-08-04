package fr.jadeveloppement.agenda.ui.dashboard;

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
import fr.jadeveloppement.agenda.components.CustomCalendar;
import fr.jadeveloppement.agenda.components.DayComponent;
import fr.jadeveloppement.agenda.components.animation.SlideAnimation;
import fr.jadeveloppement.agenda.databinding.FragmentDashboardBinding;
import fr.jadeveloppement.agenda.functions.Functions;
import fr.jadeveloppement.agenda.functions.Variables;

public class DashboardFragment extends Fragment {

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

    private CustomCalendar calendar;

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

    private void setCalendarView(){
        calendar = new CustomCalendar(getContext(), this::calendarClicked);
        dashboardAgendaContainer.addView(calendar.getMonthLayout());
        dashboardAgendaContainer.addView(calendar.getFirstLine());
        dashboardAgendaContainer.addView(calendar.getDaysLayout());
        dashboardAgendaContainer.addView(calendar.getButtonTodayLayout());

        calendar.getButtonTodayLayout().setOnClickListener(v -> {
            calendar.setDaySelected(Functions.getTodayDate());
            calendarClicked();
        });

        dashboardAgendaContainer.post(() -> agendaCalendarInitialHeight.set(dashboardAgendaContainer.getHeight()));
        dashboardPrevWeek.setOnClickListener(v -> {
            calendar.addInterval(-1, "week");
            calendarClicked();
        });

        dashboardNextWeek.setOnClickListener(v -> {
            calendar.addInterval(1, "week");
            calendarClicked();
        });
        calendarClicked();
    }

    public void calendarClicked(){
        String[] weekRange = calendar.getWeekRange();
        String weekRangeLeft = Functions.convertStdDateToLocale(weekRange[0]);
        String weekRangeRight = Functions.convertStdDateToLocale(weekRange[1]);
        String weekRangeTvTxt = weekRangeLeft + " - " + weekRangeRight;
        dashboardWeekRange.setText(weekRangeTvTxt);
        dashboardCurrentWeek.setText("Semaine  " + calendar.getWeekNumber());

        if (currentWeek != calendar.getWeekNumber()) {
            dashboardDaysLayoutContainer.removeAllViews();
            currentWeek = calendar.getWeekNumber();

            for (String day : calendar.getListOfDatesOfWeek()){
                dashboardDaysLayoutContainer.addView(new DayComponent(getContext(), Variables.days[Functions.getDayOfWeekIndex(day)], Functions.convertStdDateToLocale(day)));
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: view destroyed");
        binding = null;

    }
}