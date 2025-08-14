package fr.jadeveloppement.agenda.components;

import static java.util.Objects.isNull;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import fr.jadeveloppement.agenda.R;
import fr.jadeveloppement.agenda.functions.interfaces.TaskItemAdapterAddTaskClickedInterface;
import fr.jadeveloppement.agenda.functions.interfaces.TaskItemAdapterDeleteTaskClickedInterface;
import fr.jadeveloppement.agenda.functions.interfaces.TaskItemAdapterEditTaskClickedInterface;
import fr.jadeveloppement.agenda.functions.sqlite.functions.TasksFunctionsSQL;
import fr.jadeveloppement.agenda.functions.sqlite.tables.TasksTable;
import fr.jadeveloppement.agenda.functions.touchHelper.ItemTouchHelperAdapter;
import fr.jadeveloppement.agenda.functions.touchHelper.SimpleItemTouchHelperCallback;

public class TaskItemAdapterV2 extends RecyclerView.Adapter<TaskItemAdapterV2.ViewHolder>
        implements ItemTouchHelperAdapter,
        TaskItemAdapterEditTaskClickedInterface,
        TaskItemAdapterDeleteTaskClickedInterface {

    private final String TAG = "JADAgenda";

    private final Context context;
    private final TasksFunctionsSQL tasksfunctionssql;
    private final TaskItemAdapterDeleteTaskClickedInterface deleteListener;
    private final TaskItemAdapterAddTaskClickedInterface addTaskListener;
    private final TaskItemAdapterEditTaskClickedInterface editTaskListener;
    private List<TasksTable> itemList;

    private TaskItemAdapterV2 adapterChildren = null;
    public View view;

    public TaskItemAdapterV2(Context c, List<TasksTable> list, TaskItemAdapterDeleteTaskClickedInterface del, TaskItemAdapterAddTaskClickedInterface add, TaskItemAdapterEditTaskClickedInterface edit) {
        this.context = c;
        this.deleteListener = del;
        this.addTaskListener = add;
        this.editTaskListener = edit;
        this.tasksfunctionssql = new TasksFunctionsSQL(context);
        this.itemList = list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TasksTable currentItem = itemList.get(position);
        holder.itemTextView.setText(currentItem.label);
        holder.taskLayoutisReminded.setVisibility(currentItem.reminderDate.isBlank() ? View.GONE : View.VISIBLE);
        holder.taskLayoutisRepeated.setVisibility(currentItem.repeated == 0 ? View.GONE : View.VISIBLE);
        holder.taskLayoutCheckbox.setChecked(currentItem.done == 1);
        holder.taskLayoutTaskContainer.setBackgroundResource(currentItem.done == 1 ? R.drawable.rounded_box_orange310 : R.drawable.rounded_box_orange380);
        holder.taskLayoutTaskDoneStroke.setVisibility(currentItem.done == 1 ? View.VISIBLE : View.GONE);
        holder.taskLayoutisRepeated.setVisibility(currentItem.repeated == 1 ? View.VISIBLE : View.GONE);

        if (!isNull(currentItem.task_ID_parent)){
            holder.taskLayoutAddChild.setVisibility(View.GONE);
            holder.taskLayoutHasChildren.setVisibility(View.GONE);
        } else {
            List<TasksTable> listOfChildren = tasksfunctionssql.getTasksChildren(currentItem);
            holder.taskLayoutHasChildren.setVisibility(listOfChildren.isEmpty() ? View.GONE : View.VISIBLE);

            if (!listOfChildren.isEmpty()){
                adapterChildren = new TaskItemAdapterV2(context, listOfChildren, this, null, this);
                holder.taskLayoutTaskChildrenContainer.setAdapter(adapterChildren);
                holder.taskLayoutTaskChildrenContainer.setLayoutManager(new LinearLayoutManager(context));
                ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapterChildren);
                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                touchHelper.attachToRecyclerView(holder.taskLayoutTaskChildrenContainer);

                holder.taskLayoutHasChildren.setOnClickListener(v -> {
                    holder.taskLayoutTaskChildrenContainer.setVisibility(holder.taskLayoutTaskChildrenContainer.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                });
            }
        }

        holder.taskLayoutDeleteTask.setOnClickListener(v -> {
            if (!isNull(deleteListener)) deleteListener.taskAdapterDeleteTaskClicked(currentItem);
        });

        holder.taskLayoutAddChild.setOnClickListener(v -> {
            if (!isNull(addTaskListener)) addTaskListener.addChildrenTask(currentItem);
        });

        holder.taskLayoutEditTask.setOnClickListener(v -> {
            if (!isNull(editTaskListener)) editTaskListener.taskAdapterEditTaskClicked(currentItem);
        });

        holder.taskLayoutCheckbox.setOnClickListener(v -> {
            if (((CheckBox) v).isChecked()) makeTaskDone(position);
            else makeTaskUndone(position);
        });
    }

    /**
     * TODO - interface to make and ViewModel to update
     * @param position :
     */
    private void makeTaskDone(int position) {
        itemList.get(position).done = 1;
        tasksfunctionssql.updateTask(itemList.get(position));
        notifyItemChanged(position);
    }

    /**
     * TODO - interface to make and ViewModel to update
     * @param position :
     */
    private void makeTaskUndone(int position) {
        itemList.get(position).done = 0;
        tasksfunctionssql.updateTask(itemList.get(position));
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(itemList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(itemList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    /**
     * TODO - ItemTouchHelper interface > See why this method exists
     */
    @Override
    public void onItemDismissInterface() {

    }

    @Override
    public void onItemDropped() {
        for (int orderNumber = 1; orderNumber <= itemList.size() ; orderNumber++){
            TasksTable t = itemList.get(orderNumber-1);
            if (t.orderNumber != orderNumber) {
                t.orderNumber = orderNumber;
                tasksfunctionssql.updateTask(t);
            }
        }
    }

    /**
     * TASK CHILDREN CRUD MANAGEMENT
     */
    @Override
    public void taskAdapterDeleteTaskClicked(TasksTable tasksTable){
        if (!isNull(deleteListener))
            deleteListener.taskAdapterDeleteTaskClicked(tasksTable);
    }
    @Override
    public void taskAdapterEditTaskClicked(TasksTable tasksTable){
        if (!isNull(editTaskListener))
            editTaskListener.taskAdapterEditTaskClicked(tasksTable);
    }

    public void updateTasks(List<TasksTable> listOfTasksOfTheDay) {
        this.itemList.clear();
        this.itemList.addAll(listOfTasksOfTheDay);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout taskLayoutTaskContainer;
        TextView itemTextView;
        ImageButton taskLayoutDeleteTask, taskLayoutEditTask, taskLayoutAddChild;
        ImageView taskLayoutHasChildren, taskLayoutisReminded, taskLayoutisRepeated;
        CheckBox taskLayoutCheckbox;
        View taskLayoutTaskDoneStroke;

        RecyclerView taskLayoutTaskChildrenContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.taskLayoutLabelTv);
            taskLayoutDeleteTask = itemView.findViewById(R.id.taskLayoutDeleteTask);
            taskLayoutHasChildren = itemView.findViewById(R.id.taskLayoutHasChildren);
            taskLayoutisReminded = itemView.findViewById(R.id.taskLayoutisReminded);
            taskLayoutisRepeated = itemView.findViewById(R.id.taskLayoutisRepeated);
            taskLayoutCheckbox = itemView.findViewById(R.id.taskLayoutCheckbox);
            taskLayoutTaskContainer = itemView.findViewById(R.id.taskLayoutTaskContainer);
            taskLayoutTaskDoneStroke = itemView.findViewById(R.id.taskLayoutTaskDoneStroke);
            taskLayoutEditTask = itemView.findViewById(R.id.taskLayoutEditTask);
            taskLayoutTaskChildrenContainer = itemView.findViewById(R.id.taskLayoutTaskChildrenContainer);
            taskLayoutAddChild = itemView.findViewById(R.id.taskLayoutAddChild);
        }
    }
}
