package fr.jadeveloppement.agenda.components;

import static java.util.Objects.isNull;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import fr.jadeveloppement.agenda.R;
import fr.jadeveloppement.agenda.functions.Functions;
import fr.jadeveloppement.agenda.functions.sqlite.functions.TasksFunctionsSQL;
import fr.jadeveloppement.agenda.functions.sqlite.tables.TasksTable;
import fr.jadeveloppement.agenda.functions.touchHelper.ItemTouchHelperAdapter;
import fr.jadeveloppement.agenda.functions.touchHelper.SimpleItemTouchHelperCallback;

public class TaskItemAdapter extends RecyclerView.Adapter<TaskItemAdapter.ViewHolder> 
        implements ItemTouchHelperAdapter {

    private final String TAG = "Agenda";

    private final Context context;
    private final TasksFunctionsSQL tasksfunctionssql;
    private final String date;
    private final View viewParent;
    private List<TasksTable> itemList; // Or List<SimpleItem> if you created a data model
    private OnDeleteTaskClickListener listener;

    private final TasksTable taskParent;
    private final boolean isChildren;

    public interface OnDeleteTaskClickListener {
        void onItemDismissInterface();
    }

    public TaskItemAdapter(Context c, String d, OnDeleteTaskClickListener l, View p, boolean isC, TasksTable... tParent) {
        this.context = c;
        this.listener = l;
        this.date = d;
        this.tasksfunctionssql = new TasksFunctionsSQL(context);
        this.isChildren = isC;
        this.taskParent = isChildren && !isNull(tParent) && tParent.length > 0 ? tParent[0] : null;
        this.itemList = isChildren && !isNull(tParent) && tParent.length > 0 ? tasksfunctionssql.getTasksChildren(taskParent) : tasksfunctionssql.getTaskOfPeriod(date);
        this.viewParent = p;
    }

    public void addItem(TasksTable newItem) {
        TasksTable newTask = tasksfunctionssql.insertTask(newItem);
        if (isNull(newTask)) Toast.makeText(context, "Une erreur est survenue, veuillez réessayer", Toast.LENGTH_SHORT).show();
        else {
            itemList.add(newTask);
            notifyItemInserted(itemList.size() - 1); // Notify that an item has been inserted at the last position
        }
    }

    public View view;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TasksTable currentItem = itemList.get(position);
        List<TasksTable> itemChildren = tasksfunctionssql.getTasksChildren(currentItem);
        holder.itemTextView.setText(currentItem.label);
        holder.taskLayoutHasChildren.setVisibility(itemChildren.isEmpty() ? View.GONE : View.VISIBLE);
        holder.taskLayoutisReminded.setVisibility(currentItem.reminderDate.isBlank() ? View.GONE : View.VISIBLE);
        holder.taskLayoutisRepeated.setVisibility(currentItem.repeated == 0 ? View.GONE : View.VISIBLE);
        holder.taskLayoutCheckbox.setChecked(currentItem.done == 1);
        holder.taskLayoutTaskContainer.setBackgroundResource(currentItem.done == 1 ? R.drawable.rounded_box_orange310 : R.drawable.rounded_box_orange380);
        holder.taskLayoutTaskDoneStroke.setVisibility(currentItem.done == 1 ? View.VISIBLE : View.GONE);
        holder.taskLayoutisRepeated.setVisibility(currentItem.repeated == 1 ? View.VISIBLE : View.GONE);

        if (!itemChildren.isEmpty()){
            TaskItemAdapter childrenAdapter = new TaskItemAdapter(context, date, null, view, true, currentItem);
            holder.taskLayoutTaskChildrenContainer.setAdapter(childrenAdapter);
            holder.taskLayoutTaskChildrenContainer.setLayoutManager(new LinearLayoutManager(context));
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(childrenAdapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(holder.taskLayoutTaskChildrenContainer);
            if (childrenAdapter.getItemCount() > 0){
                holder.taskLayoutHasChildren.setVisibility(View.VISIBLE);
                view.setOnClickListener(v -> {
                    holder.taskLayoutTaskChildrenContainer.setVisibility(holder.taskLayoutTaskChildrenContainer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                });
            } else {
                holder.taskLayoutHasChildren.setVisibility(View.VISIBLE);
            }
        }

        holder.taskLayoutDeleteTask.setOnClickListener(v -> {
            onItemDismiss(currentItem);
            if (!isNull(listener)) listener.onItemDismissInterface();
        });

        holder.taskLayoutEditTask.setOnClickListener(v -> {
            Popup editPopup = new Popup(context, viewParent, this);
            PopupTask editTaskContent = new PopupTask(context, editPopup.getContentView(), currentItem);
            editPopup.addContent(editTaskContent);
            editTaskContent.getBtnSave().setOnClickListener(v1 -> {
                TasksTable editedTask = currentItem;
                editedTask.label = editTaskContent.getTaskLabel().getText().toString();
                tasksfunctionssql.updateTask(editedTask);
                notifyItemChanged(itemList.indexOf(currentItem));
                editPopup.closePopup();
            });
        });

        holder.taskLayoutAddChild.setOnClickListener(v -> {
            Popup addChildren = new Popup(context, viewParent, this);
            PopupTask addChildrenContent = new PopupTask(context, addChildren.getContentView(), currentItem, currentItem);
            addChildren.addContent(addChildrenContent);
            addChildrenContent.getBtnSave().setOnClickListener(v1 -> {
                String labelChild = addChildrenContent.getTaskLabel().getText().toString();
                if (labelChild.isBlank()) Toast.makeText(context, "Le label ne peut pas être vide", Toast.LENGTH_LONG).show();
                else {
                    TasksTable newTaskChildren = new TasksTable();
                    newTaskChildren.label = labelChild;
                    newTaskChildren.date = currentItem.date;
                    newTaskChildren.task_ID_parent = currentItem.task_ID;
                    tasksfunctionssql.insertTask(newTaskChildren);
                    addChildren.closePopup();
                    notifyItemChanged(itemList.indexOf(currentItem));
                }
            });
        });

        holder.taskLayoutCheckbox.setOnClickListener(v -> {
            if (((CheckBox) v).isChecked()) makeTaskDone(position);
            else makeTaskUndone(position);
        });
    }

    private void makeTaskDone(int position) {
        itemList.get(position).done = 1;
        tasksfunctionssql.updateTask(itemList.get(position));
        notifyItemChanged(position);
    }

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

    @Override
    public void onItemDismissInterface() {

    }

    public void onItemDismiss(TasksTable t) {
        int position = itemList.indexOf(t);
        tasksfunctionssql.deleteTask(itemList.get(position));
        itemList.remove(position);
        notifyItemRemoved(position);
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
