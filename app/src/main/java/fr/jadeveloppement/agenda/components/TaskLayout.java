package fr.jadeveloppement.agenda.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import fr.jadeveloppement.agenda.R;
import fr.jadeveloppement.agenda.functions.sqlite.functions.TasksFunctionsSQL;
import fr.jadeveloppement.agenda.functions.sqlite.tables.TasksTable;

public class TaskLayout extends LinearLayout {

    private final Context context;
    private TasksTable task;
    private TasksFunctionsSQL tasksFunctionsSQL;
    private View taskLayout;

    public TaskLayout(Context c){
        super(c);
        this.context = c;
    }

    public TaskLayout(@NonNull Context c, @NonNull TasksTable t){
        super(c);
        this.context = c;
        this.tasksFunctionsSQL = new TasksFunctionsSQL(context);
        this.taskLayout = LayoutInflater.from(context).inflate(R.layout.task_layout, this, true);
        this.task = t;

        initView();
    }

    private CheckBox taskLayoutCheckbox;
    private TextView taskLayoutLabelTv;
    private ImageView taskLayoutHasChildren,
            taskLayoutisReminded,
            taskLayoutisRepeated;

    private ImageButton taskLayoutDeleteTask,
            taskLayoutEditTask,
            taskLayoutAddChild;

    private View taskLayoutTaskDoneStroke;
    private final int INVISIBLE = View.GONE;

    private void initView(){
        taskLayoutCheckbox = this.findViewById(R.id.taskLayoutCheckbox);
        taskLayoutLabelTv = this.findViewById(R.id.taskLayoutLabelTv);
        taskLayoutHasChildren = this.findViewById(R.id.taskLayoutHasChildren);
        taskLayoutisReminded = this.findViewById(R.id.taskLayoutisReminded);
        taskLayoutisRepeated = this.findViewById(R.id.taskLayoutisRepeated);
        taskLayoutDeleteTask = this.findViewById(R.id.taskLayoutDeleteTask);
        taskLayoutEditTask = this.findViewById(R.id.taskLayoutEditTask);
        taskLayoutAddChild = this.findViewById(R.id.taskLayoutAddChild);
        taskLayoutTaskDoneStroke = this.findViewById(R.id.taskLayoutTaskDoneStroke);

        taskLayoutCheckbox.setVisibility(INVISIBLE);
        taskLayoutHasChildren.setVisibility(INVISIBLE);
        taskLayoutisReminded.setVisibility(INVISIBLE);
        taskLayoutisRepeated.setVisibility(INVISIBLE);
        taskLayoutDeleteTask.setVisibility(INVISIBLE);
        taskLayoutEditTask.setVisibility(INVISIBLE);
        taskLayoutAddChild.setVisibility(INVISIBLE);
        taskLayoutTaskDoneStroke.setVisibility(INVISIBLE);

        taskLayoutLabelTv.setText(task.label);
    }
}
