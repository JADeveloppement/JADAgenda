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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import fr.jadeveloppement.agenda.MainActivity;
import fr.jadeveloppement.agenda.components.DayComponent;
import fr.jadeveloppement.agenda.components.Popup;
import fr.jadeveloppement.agenda.components.PopupTask;
import fr.jadeveloppement.agenda.components.animation.SlideAnimation;
import fr.jadeveloppement.agenda.databinding.FragmentDashboardBinding;

import fr.jadeveloppement.agenda.functions.Functions;
import fr.jadeveloppement.agenda.functions.Variables;
import fr.jadeveloppement.agenda.functions.interfaces.TaskItemAdapterAddTaskClickedInterface;
import fr.jadeveloppement.agenda.functions.interfaces.TaskItemAdapterDeleteTaskClickedInterface;
import fr.jadeveloppement.agenda.functions.interfaces.TaskItemAdapterEditTaskClickedInterface;
import fr.jadeveloppement.agenda.functions.sqlite.tables.TasksTable;
import fr.jadeveloppement.jadcustomcalendar.CustomCalendar;

public class DashboardFragment extends Fragment implements
        CustomCalendar.DateChanged,
        DayComponent.DayAddTaskInterface,
        TaskItemAdapterDeleteTaskClickedInterface,
        TaskItemAdapterAddTaskClickedInterface,
        TaskItemAdapterEditTaskClickedInterface {

    private String TAG = "JADAgenda";

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

    private DashboardViewModel dashboardViewModel;;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        currentWeek = -2;
        agendaCalendarInitialHeight = new AtomicInteger(-2);

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
        updateWeekComponentUI();

        setObservers();

        return binding.getRoot();
    }

    /**
     * Initialize the animation when clicking on WeekComponent to make dis/appear the CustomCalendarView
     */
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

    /**
     * Set the event for WeekComponent when clicking on next button or previous button
     */
    private void setDashboardEvents() {
        dashboardPrevWeek.setOnClickListener(v -> {
            dashboardAgendaContainer.addInterval(-1, "week");
        });

        dashboardNextWeek.setOnClickListener(v -> {
            dashboardAgendaContainer.addInterval(1, "week");
        });
    }

    /**
     * Initialize the CustomCalendar to display
     */
    private void setCalendarView(){
        dashboardAgendaContainer.post(() -> agendaCalendarInitialHeight.set(dashboardAgendaContainer.getHeight()));
        dashboardAgendaContainer.setListener(this);
    }

    /**
     * Initialize the content of the weekcomponent and update the DayContainer with the correct day of the week
     * selected in the WeekComponent
     */
    private void updateWeekComponentUI(){
        String[] weekRange = dashboardAgendaContainer.getWeekRange();
        String weekRangeLeft = Functions.convertStdDateToLocale(weekRange[0]);
        String weekRangeRight = Functions.convertStdDateToLocale(weekRange[1]);
        String weekRangeTvTxt = weekRangeLeft + " - " + weekRangeRight;
        dashboardWeekRange.setText(weekRangeTvTxt);
        dashboardCurrentWeek.setText("Semaine  " + dashboardAgendaContainer.getWeekNumber());

        if (currentWeek != dashboardAgendaContainer.getWeekNumber()) {
            dashboardDaysLayoutContainer.removeAllViews();
            currentWeek = dashboardAgendaContainer.getWeekNumber();
            dashboardViewModel.loadTasksForWeek(dashboardAgendaContainer.getListOfDatesOfWeek());
        }
    }

    /**
     * Set obersvers for the ViewModel of the application.
     */
    private void setObservers() {
        dashboardViewModel.getTasksForWeek().observe(getViewLifecycleOwner(), tasksByDate -> {
            if (tasksByDate == null) return;

            dashboardDaysLayoutContainer.removeAllViews();
            int index = 0;
            List<String> daysOfWeek = dashboardAgendaContainer.getListOfDatesOfWeek();

            for (String day : daysOfWeek) {
                DayComponent dayComponent = new DayComponent(requireContext(), Variables.days[index], day, this, this, this, this);

                List<TasksTable> tasksForThisDay = tasksByDate.get(day);

                dayComponent.setTasks(tasksForThisDay != null && !tasksForThisDay.isEmpty() ? tasksForThisDay : new ArrayList<>());

                dashboardDaysLayoutContainer.addView(dayComponent);
                index++;
            }
        });
    }

    @Override
    public void selectedDayChanged(){
        if (!isNull(dashboardAgendaContainer)){
            updateWeekComponentUI();
        }
    }

    @Override
    public void addTaskToDayComponent(String date){
        Popup addTask = new Popup(requireContext());
        PopupTask contentPopup = new PopupTask(requireContext(), null, null);
        addTask.addContent(contentPopup);
        contentPopup.getTaskDate().setText(Functions.convertStdDateToLocale(date));
        contentPopup.getBtnSave().setOnClickListener(v1 -> {
            String label = contentPopup.getTaskLabel().getText().toString();

            if (label.isBlank()){
                Snackbar.make(v1, "Le label ne peut pas être vide.", Snackbar.LENGTH_SHORT).show();
                return;
            }

            TasksTable newTask = new TasksTable(label, date);

            if (contentPopup.isReminderEnabled){
                if (contentPopup.getReminderDate().isBlank()){
                    Snackbar.make(v1, "Veuillez sélectionner une date de rappel.", Snackbar.LENGTH_SHORT).show();
                    return ;
                }
                newTask.notification_ID = String.valueOf((int) System.currentTimeMillis());
                Functions.createReminder(requireContext(), contentPopup.getReminderDate(), newTask);
            }

            if (contentPopup.isRepeatEnabled){
                int repeatFrequency = (int) contentPopup.getNewTaskRepeatSpinner().getSelectedItemId();
                newTask.repeatFrequency = String.valueOf(repeatFrequency);
                newTask.repeated = 1;
            }

            dashboardViewModel.insertNewTask(newTask);
            dashboardViewModel.loadTasksForWeek(dashboardAgendaContainer.getListOfDatesOfWeek());

            contentPopup.getTaskLabel().setText("");
            Snackbar.make(v1, "Tâche rajoutée avec succès", Snackbar.LENGTH_SHORT).show();
        });
    }

    @Override
    public void addChildrenTask(TasksTable t){
        Popup addTask = new Popup(requireContext());
        PopupTask contentPopup = new PopupTask(requireContext(), null, null, t);
        contentPopup.enableReminder(false);
        contentPopup.enableRepeat(false);

        addTask.addContent(contentPopup);

        contentPopup.getTaskDate().setText(Functions.convertStdDateToLocale(t.date));

        contentPopup.getBtnSave().setOnClickListener(v1 -> {
            String label = contentPopup.getTaskLabel().getText().toString();

            if (label.isBlank()){
                Snackbar.make(v1, "Le label ne peut pas être vide.", Snackbar.LENGTH_SHORT).show();
                return;
            }

            TasksTable newTask = new TasksTable(label, t.date);
            newTask.task_ID_parent = t.task_ID;

            dashboardViewModel.insertNewTask(newTask);
            dashboardViewModel.loadTasksForWeek(dashboardAgendaContainer.getListOfDatesOfWeek());

            contentPopup.getTaskLabel().setText("");
            Snackbar.make(v1, "Tâche rajoutée avec succès", Snackbar.LENGTH_SHORT).show();
        });
    }

    @Override
    public void taskAdapterEditTaskClicked(@NonNull TasksTable tasksTable){
        Log.d(TAG, "DashboardFragment > taskAdapterEditTaskClicked: edit task : " + tasksTable);
    }

    @Override
    public void taskAdapterDeleteTaskClicked(@NonNull TasksTable tasksTable){
        dashboardViewModel.deleteTask(tasksTable);
        dashboardViewModel.loadTasksForWeek(dashboardAgendaContainer.getListOfDatesOfWeek());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }
}