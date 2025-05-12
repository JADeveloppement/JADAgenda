package fr.jadeveloppement.agenda.components;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.jadeveloppement.agenda.R;
import fr.jadeveloppement.agenda.functions.Functions;
import fr.jadeveloppement.agenda.functions.sqlite.tables.TasksTable;
import fr.jadeveloppement.agenda.functions.touchHelper.SimpleItemTouchHelperCallback;

public class DayComponent extends LinearLayout implements TaskItemAdapter.OnDeleteTaskClickListener {

    private final String TAG = "agenda";

    private final Context context;
    private final View view;
    private String dateDate;
    private String dateName;
    private boolean tasksDisplayed = false;

    public DayComponent(@NonNull Context c){
        super(c);
        this.context = c;
        this.view = LayoutInflater.from(context).inflate(R.layout.day_layout, this, false);

        initLayout();
    }

    public DayComponent(@NonNull Context c, @NonNull String dateN, @NonNull String dateD){
        super(c);
        this.context = c;
        this.view = LayoutInflater.from(context).inflate(R.layout.day_layout, this, true);
        this.dateDate = dateD;
        this.dateName = dateN;

        initLayout();
    }

    private TextView dayLayoutDayName, dayLayoutDayDate, dayLayoutNbTask;
    private RecyclerView dayLayoutTasksOfDayContainer;
    private ImageButton dayLayouAddTask;

    private TaskItemAdapter adapter;

    private void initLayout(){
        dayLayoutDayName = this.findViewById(R.id.dayLayoutDayName);
        dayLayoutDayDate = this.findViewById(R.id.dayLayoutDayDate);
        dayLayoutNbTask = this.findViewById(R.id.dayLayoutNbTask);
        dayLayouAddTask = this.findViewById(R.id.dayLayouAddTask);

        dayLayoutDayName.setText(dateName);
        dayLayoutDayDate.setText(dateDate);

        adapter = new TaskItemAdapter(context, Functions.convertLocaleDateToStd(dateDate), this, view, false);

        dayLayoutTasksOfDayContainer = this.findViewById(R.id.dayLayoutTasksOfDayContainer);

        dayLayoutNbTask.setVisibility(adapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
        dayLayoutTasksOfDayContainer.setVisibility(adapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);

        dayLayoutNbTask.setText(String.valueOf(adapter.getItemCount()));

        dayLayoutTasksOfDayContainer.setAdapter(adapter);
        dayLayoutTasksOfDayContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(dayLayoutTasksOfDayContainer);

        dayLayoutTasksOfDayContainer.setVisibility(View.GONE);

        if (Functions.convertLocaleDateToStd(dateDate).equalsIgnoreCase(Functions.getTodayDate()))
            toggleListOfTasks();

        view.setOnClickListener(v -> {
            toggleListOfTasks();
        });

        dayLayouAddTask.setOnClickListener(v -> {
            Popup addTask = new Popup(getContext(), view, adapter);
            PopupTask contentPopup = new PopupTask(context, view, null);
            addTask.addContent(contentPopup);
            contentPopup.getTaskDate().setText(dateDate);
            contentPopup.getBtnSave().setOnClickListener(v1 -> {
                String label = contentPopup.getTaskLabel().getText().toString();
                String date = Functions.convertLocaleDateToStd(contentPopup.getTaskDate().getText().toString());
                int repeatFrequency = -1;
                if (label.isBlank()) makeToast("Le label ne peut pas être vide.");
                else {
                    TasksTable newTask = new TasksTable();
                    newTask.label = label;
                    newTask.date = date;

                    boolean formValid = true;

                    if (contentPopup.isReminderEnabled){
                        if (contentPopup.reminderDate.isBlank()){
                            makeToast("Rappel activé : veuillez sélectionner une date de rappel.");
                            formValid = false;
                        }
                        else {
                            newTask.notification_ID = String.valueOf((int) System.currentTimeMillis());
                            Functions.createReminder(context, contentPopup.reminderDate, newTask);
                        }
                    }

                    if (contentPopup.isRepeatEnabled){
                        repeatFrequency = (int) contentPopup.getNewTaskRepeatSpinner().getSelectedItemId();
                        newTask.repeatFrequency = String.valueOf(repeatFrequency);
                        newTask.repeated = 1;
                    }

                    if (formValid){
                        adapter.addItem(newTask);
                        contentPopup.getTaskLabel().setText("");
                        updateNbTaskTv();
                        if (adapter.getItemCount() == 0) dayLayoutNbTask.setVisibility(View.GONE);
                        else {
                            dayLayoutNbTask.setText(String.valueOf(adapter.getItemCount()));
                            dayLayoutNbTask.setVisibility(View.VISIBLE);
                        }
                        makeToast("Tâche rajoutée avec succès");
                    } else {
                        makeToast("Des informations sont manquantes, veuillez les complétez.");
                    }
                }
            });
        });
    }

    private void updateNbTaskTv(){
        if (adapter.getItemCount() == 0) dayLayoutNbTask.setVisibility(View.GONE);
        else {
            dayLayoutNbTask.setText(String.valueOf(adapter.getItemCount()));
            dayLayoutNbTask.setVisibility(View.VISIBLE);
        }
    }

    private void makeToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    private void toggleListOfTasks(){
        tasksDisplayed = !tasksDisplayed;
        if (tasksDisplayed){
            this.setBackgroundColor(context.getColor(R.color.orange450));
            dayLayoutDayDate.setBackgroundResource(R.drawable.rounded_box_orange380);
            dayLayoutDayDate.setTextColor(context.getColor(R.color.white));
            dayLayoutDayName.setTextColor(context.getColor(R.color.white));
            dayLayoutTasksOfDayContainer.setVisibility(View.VISIBLE);
        } else {
            this.setBackgroundColor(context.getColor(R.color.orange410));
            dayLayoutDayName.setTextColor(context.getColor(R.color.orange1));
            dayLayoutDayDate.setBackgroundResource(android.R.color.transparent);
            dayLayoutDayDate.setTextColor(context.getColor(R.color.orange1));
            dayLayoutTasksOfDayContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemDismissInterface() {
        updateNbTaskTv();
    }
}
