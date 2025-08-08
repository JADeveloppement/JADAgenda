package fr.jadeveloppement.agenda.ui.dashboard;

import static java.lang.Integer.parseInt;
import static java.util.Objects.isNull;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.sql.Time;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import fr.jadeveloppement.agenda.MainActivity;
import fr.jadeveloppement.agenda.components.DayComponent;
import fr.jadeveloppement.agenda.components.animation.SlideAnimation;
import fr.jadeveloppement.agenda.databinding.FragmentDashboardBinding;

import fr.jadeveloppement.agenda.functions.Functions;
import fr.jadeveloppement.agenda.functions.NotificationHelper;
import fr.jadeveloppement.agenda.functions.Variables;
import fr.jadeveloppement.agenda.functions.broadcast.ReminderBroadcastReceiver;
import fr.jadeveloppement.jadcustomcalendar.CustomCalendar;

public class DashboardFragment extends Fragment implements CustomCalendar.DateChanged {

    private String TAG = "agenda";

    private FragmentDashboardBinding binding;

    private CustomCalendar dashboardAgendaContainer;
    private LinearLayout dashboardPrevWeek,
            dashboardNextWeek,
            dashboardWeekLayoutContainer,
            dashboardDaysLayoutContainer;

    private AtomicInteger agendaCalendarInitialHeight;

    private TextView dashboardCurrentWeek,
            dashboardWeekRange;

    private ScrollView dashboardScrollView;

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

        initDashboardUI();
        setDashboardEvents();
        setCalendarView();
        updateTasksUI();

        return root;
    }

    private void initDashboardUI() {
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
    }

    private void setCalendarView(){
        dashboardAgendaContainer.post(() -> agendaCalendarInitialHeight.set(dashboardAgendaContainer.getHeight()));
        dashboardAgendaContainer.setListener(this);
    }

    private void setDashboardEvents() {
        dashboardPrevWeek.setOnClickListener(v -> {
            dashboardAgendaContainer.addInterval(-1, "week");
        });

        dashboardNextWeek.setOnClickListener(v -> {
            dashboardAgendaContainer.addInterval(1, "week");
        });
    }

    private void updateTasksUI(){
        String[] weekRange = dashboardAgendaContainer.getWeekRange();
        String weekRangeLeft = Functions.convertStdDateToLocale(weekRange[0]);
        String weekRangeRight = Functions.convertStdDateToLocale(weekRange[1]);
        String weekRangeTvTxt = weekRangeLeft + " - " + weekRangeRight;
        dashboardWeekRange.setText(weekRangeTvTxt);
        dashboardCurrentWeek.setText("Semaine  " + dashboardAgendaContainer.getWeekNumber());

        if (currentWeek != dashboardAgendaContainer.getWeekNumber()) {
            dashboardDaysLayoutContainer.removeAllViews();
            currentWeek = dashboardAgendaContainer.getWeekNumber();

            for (String day : dashboardAgendaContainer.getListOfDatesOfWeek()){
                dashboardDaysLayoutContainer.addView(new DayComponent(requireContext(), Variables.days[Functions.getDayOfWeekIndex(day)], Functions.convertStdDateToLocale(day)));
            }
        }
    }

    @Override
    public void selectedDayChanged(){
        if (!isNull(dashboardAgendaContainer)){
            updateTasksUI();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }
}