package fr.jadeveloppement.agenda.components;

import static java.util.Objects.isNull;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.Calendar;

import fr.jadeveloppement.agenda.R;
import fr.jadeveloppement.agenda.functions.Functions;
import fr.jadeveloppement.agenda.functions.TextWatcherFunction;
import fr.jadeveloppement.agenda.functions.sqlite.tables.TasksTable;

public class PopupTask extends LinearLayout {

    private final Context context;
    private TasksTable task;
    private View viewParent;
    private View popupTaskLayout;
    private TasksTable task_parent;

    public PopupTask(Context c){
        super(c);
        this.context = c;
    }

    public PopupTask(Context c, View p, TasksTable t, TasksTable... task_p){
        super(c);
        this.context = c;
        this.viewParent = p;
        this.popupTaskLayout = LayoutInflater.from(getContext()).inflate(R.layout.popup_task_layout, this, true);
        this.task = t;
        this.task_parent = !isNull(task_p) && task_p.length > 0 ? task_p[0] : null;

        initPopup();
    }

    private EditText addNewTaskLabel;
    private TextView addNewTaskDateTv, addNewTaskSave,addNewTaskReminderDateTv, addNewTaskParentLabel, addNewTaskNotificationPermissionTv;
    private ImageButton addNewTaskReminderEnabled, addNewTaskRepeatEnabled;
    private Spinner addNewTaskRepeatSpinner;

    private LinearLayout addNewTaskReminderContainer, addNewTaskRepeatContainer, addNewTaskParentContainer;

    public boolean isReminderEnabled = false;
    public boolean isRepeatEnabled = false;
    public String reminderDate = "";

    private void initPopup(){
        addNewTaskLabel = this.findViewById(R.id.addNewTaskLabel);
        addNewTaskDateTv = this.findViewById(R.id.addNewTaskDateTv);
        addNewTaskSave = this.findViewById(R.id.addNewTaskSave);
        addNewTaskReminderEnabled = this.findViewById(R.id.addNewTaskReminderEnabled);
        addNewTaskReminderContainer = this.findViewById(R.id.addNewTaskReminderContainer);
        addNewTaskRepeatEnabled = this.findViewById(R.id.addNewTaskRepeatEnabled);
        addNewTaskRepeatContainer = this.findViewById(R.id.addNewTaskRepeatContainer);
        addNewTaskRepeatSpinner = this.findViewById(R.id.addNewTaskRepeatSpinner);
        addNewTaskParentContainer = this.findViewById(R.id.addNewTaskParentContainer);
        addNewTaskParentLabel = this.findViewById(R.id.addNewTaskParentLabel);
        addNewTaskNotificationPermissionTv = this.findViewById(R.id.addNewTaskNotificationPermissionTv);

        addNewTaskLabel.addTextChangedListener(new TextWatcherFunction(addNewTaskLabel));

        addNewTaskReminderDateTv = this.findViewById(R.id.addNewTaskReminderDateTv);

        ArrayAdapter<CharSequence> repeatAdapter = ArrayAdapter.createFromResource(context, R.array.frequency_options, android.R.layout.simple_spinner_item);
        repeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addNewTaskRepeatSpinner.setAdapter(repeatAdapter);

        if (!isNull(task_parent)){
            addNewTaskDateTv.setText(Functions.convertStdDateToLocale(task_parent.date));
            addNewTaskParentContainer.setVisibility(View.VISIBLE);
            addNewTaskParentLabel.setText(task_parent.label);
        } else if (!isNull(task)){
            addNewTaskLabel.setText(task.label);
            addNewTaskDateTv.setText(Functions.convertStdDateToLocale(task.date));
        }

        eventsPopup();
    }

    private boolean reminderEnabled = false;
    private boolean repeatEnabled = false;

    private void eventsPopup(){
        addNewTaskReminderEnabled.setOnClickListener(v -> {
            reminderEnabled = !reminderEnabled;
            isReminderEnabled = reminderEnabled;
            addNewTaskReminderEnabled.setBackgroundResource(reminderEnabled ? R.drawable.alarm_bell : R.drawable.alarm_bell_greyscale);
            addNewTaskReminderContainer.setVisibility(reminderEnabled ? View.VISIBLE : View.GONE);
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                addNewTaskNotificationPermissionTv.setVisibility(View.VISIBLE);
                addNewTaskReminderContainer.setClickable(false);
            }
        });

        addNewTaskRepeatEnabled.setOnClickListener(v -> {
            repeatEnabled = !repeatEnabled;
            isRepeatEnabled = repeatEnabled;
            addNewTaskRepeatEnabled.setBackgroundResource(repeatEnabled ? R.drawable.repeat : R.drawable.repeat_greyscale);
            addNewTaskRepeatContainer.setVisibility(repeatEnabled ? View.VISIBLE : View.GONE);
        });

        addNewTaskReminderContainer.setOnClickListener(v -> {
            Popup pickDate = new Popup(context, viewParent, null);
            CustomCalendarPicker customCalPicker = new CustomCalendarPicker(context);
            customCalPicker.setPickerTitle("Date de rappel");
            pickDate.addContent(customCalPicker.getCalendarLayout());
            customCalPicker.getBtnSave().setOnClickListener(v1 -> {
                addNewTaskReminderDateTv.setText(customCalPicker.getDateTime());
                reminderDate = customCalPicker.getDateTime();
                pickDate.closePopup();
            });
        });
    }

    public TextView getBtnSave(){
        return addNewTaskSave;
    }

    public TextView getTaskLabel() {
        return addNewTaskLabel;
    }

    public TextView getTaskDate() {
        return addNewTaskDateTv;
    }

    public Spinner getNewTaskRepeatSpinner(){
        return addNewTaskRepeatSpinner;
    }
}
