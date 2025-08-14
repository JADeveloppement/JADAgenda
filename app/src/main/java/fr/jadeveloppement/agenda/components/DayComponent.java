package fr.jadeveloppement.agenda.components;

import static java.util.Objects.isNull;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import fr.jadeveloppement.agenda.R;
import fr.jadeveloppement.agenda.functions.Functions;
import fr.jadeveloppement.agenda.functions.interfaces.TaskItemAdapterAddTaskClickedInterface;
import fr.jadeveloppement.agenda.functions.interfaces.TaskItemAdapterDeleteTaskClickedInterface;
import fr.jadeveloppement.agenda.functions.interfaces.TaskItemAdapterEditTaskClickedInterface;
import fr.jadeveloppement.agenda.functions.sqlite.tables.TasksTable;
import fr.jadeveloppement.agenda.functions.touchHelper.SimpleItemTouchHelperCallback;

public class DayComponent extends LinearLayout implements
        TaskItemAdapterDeleteTaskClickedInterface,
        TaskItemAdapterAddTaskClickedInterface,
        TaskItemAdapterEditTaskClickedInterface{

    private final String TAG = "JADAgenda";
    private final Context context;
    private final View view;
    private TaskItemAdapterAddTaskClickedInterface addChildTask;
    private TaskItemAdapterEditTaskClickedInterface editTask;
    private TaskItemAdapterDeleteTaskClickedInterface deleteTask;
    private String dateDate;
    private String dateDayName;
    private boolean tasksDisplayed = false;
    private TextView dayLayoutDayName, dayLayoutDayDate, dayLayoutNbTask;
    private RecyclerView dayLayoutTasksOfDayContainer;
    private ImageButton dayLayoutAddTask;
    private TaskItemAdapterV2 adapter;
    private List<TasksTable> listOfTasksOfTheDay;

    public interface DayAddTaskInterface {
        void addTaskToDayComponent(String date);
    }

    private DayAddTaskInterface addTaskToDay;

    public DayComponent(@NonNull Context c){
        super(c);
        this.context = c;
        this.view = LayoutInflater.from(context).inflate(R.layout.day_layout, this, false);

        initLayout();
    }

    /**
     * Constructor to create a DayComponent
     * @param c : context
     * @param dateN : date name (Monday, TuesDay, ...)
     * @param dateD : date of the day (format YYYY-MM-DD)
     * @param addL : callback when we want to add a task to this DayComponent
     */
    public DayComponent(@NonNull Context c, @NonNull String dateN, @NonNull String dateD,
                        @NonNull DayAddTaskInterface addL,
                        @NonNull TaskItemAdapterAddTaskClickedInterface addChild,
                        @NonNull TaskItemAdapterEditTaskClickedInterface editT,
                        @NonNull TaskItemAdapterDeleteTaskClickedInterface deleteT){
        super(c);
        this.context = c;
        this.view = LayoutInflater.from(context).inflate(R.layout.day_layout, this, true);
        this.dateDayName = dateN;
        this.dateDate = dateD;
        this.addTaskToDay = addL;
        this.addChildTask = addChild;
        this.editTask = editT;
        this.deleteTask = deleteT;

        initLayout();
    }

    /**
     * Initializes the DayComponent
     */
    private void initLayout(){
        findViews();
        setTextValuesOfDates();
        setDayEvents();
        setAdapter();
    }

    /**
     * Iniitializes the differents elements of the view
     */
    private void findViews() {
        dayLayoutDayName = this.findViewById(R.id.dayLayoutDayName);
        dayLayoutDayDate = this.findViewById(R.id.dayLayoutDayDate);
        dayLayoutNbTask = this.findViewById(R.id.dayLayoutNbTask);
        dayLayoutAddTask = this.findViewById(R.id.dayLayoutAddTask);
        dayLayoutTasksOfDayContainer = this.findViewById(R.id.dayLayoutTasksOfDayContainer);

    }

    /**
     * Display the text of the day and their respective date
     */
    private void setTextValuesOfDates() {
        dayLayoutDayName.setText(dateDayName);
        dayLayoutDayDate.setText(Functions.convertStdDateToLocale(dateDate));
    }

    /**
     * Manages events of the DayComponent
     * > When clicking on view toggle the view of tasks of this day
     * > When clicking on the button to add a task, call the interface
     */
    private void setDayEvents() {
        view.setOnClickListener(v -> toggleListOfTasks());
        dayLayoutAddTask.setOnClickListener(v -> addTaskToDay.addTaskToDayComponent(dateDate));
    }

    /**
     * Set the tasks of the day
     * @param tasksForThisDay : list of tasks to store for the day
     */
    public void setTasks(List<TasksTable> tasksForThisDay) {
        listOfTasksOfTheDay = tasksForThisDay;
        updateNbTaskTv();
        adapter.updateTasks(listOfTasksOfTheDay);
    }

    /**
     * Initializes the adapter to displauy inside the recycler view for the tasks of the day
     */
    private void setAdapter() {
        adapter = new TaskItemAdapterV2(context, new ArrayList<>(), this, this, this);
        dayLayoutNbTask.setText(String.valueOf(adapter.getItemCount()));

        dayLayoutTasksOfDayContainer.setAdapter(adapter);
        dayLayoutTasksOfDayContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        dayLayoutTasksOfDayContainer.setVisibility(View.GONE);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(dayLayoutTasksOfDayContainer);
    }

    /**
     * Display the number of task for this day
     */
    private void updateNbTaskTv(){
        if (listOfTasksOfTheDay.isEmpty()) dayLayoutNbTask.setVisibility(View.GONE);
        else {
            dayLayoutNbTask.setVisibility(View.VISIBLE);
            dayLayoutNbTask.setText(String.valueOf(listOfTasksOfTheDay.size()));
        }
    }

    /**
     * Toggle the visibility of the tasks of a DayComponent
     */
    private void toggleListOfTasks(){
        tasksDisplayed = !tasksDisplayed;
        if (tasksDisplayed){
            this.setBackgroundColor(context.getColor(R.color.orange450));
            dayLayoutDayDate.setBackgroundResource(R.drawable.rounded_box_orange380);
            dayLayoutDayDate.setTextColor(context.getColor(R.color.white));
            dayLayoutDayName.setTextColor(context.getColor(R.color.white));
            dayLayoutTasksOfDayContainer.setVisibility(View.VISIBLE);
        } else {
            this.setBackgroundColor(0);
            dayLayoutDayName.setTextColor(context.getColor(R.color.orange1));
            dayLayoutDayDate.setBackgroundResource(android.R.color.transparent);
            dayLayoutDayDate.setTextColor(context.getColor(R.color.orange1));
            dayLayoutTasksOfDayContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void addChildrenTask(TasksTable t){
        if (!isNull(addChildTask)) addChildTask.addChildrenTask(t);
    }

    @Override
    public void taskAdapterEditTaskClicked(TasksTable tasksTable){
        if (!isNull(editTask)) editTask.taskAdapterEditTaskClicked(tasksTable);
    }

    @Override
    public void taskAdapterDeleteTaskClicked(TasksTable tasksTable){
        if (!isNull(deleteTask)) deleteTask.taskAdapterDeleteTaskClicked(tasksTable);
    }
}
