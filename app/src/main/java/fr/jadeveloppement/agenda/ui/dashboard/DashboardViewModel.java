package fr.jadeveloppement.agenda.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.jadeveloppement.agenda.functions.sqlite.functions.TasksFunctionsSQL;
import fr.jadeveloppement.agenda.functions.sqlite.tables.TasksTable;

public class DashboardViewModel extends AndroidViewModel {

    private final MutableLiveData<Map<String, List<TasksTable>>> tasksForWeek = new MutableLiveData<>();

    private final TasksFunctionsSQL tasksFunctionsSQL;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        this.tasksFunctionsSQL = new TasksFunctionsSQL(application.getApplicationContext());
    }

    public LiveData<Map<String, List<TasksTable>>> getTasksForWeek() {
        return tasksForWeek;
    }

    public void loadTasksForWeek(List<String> dates) {
        if (dates == null || dates.isEmpty()) {
            return;
        }

        List<TasksTable> allTasksInPeriod = tasksFunctionsSQL.getTasksOfWeek(dates);

        Map<String, List<TasksTable>> tasksByDateMap = new HashMap<>();

        for (TasksTable task : allTasksInPeriod) {
            String taskDate = task.date;
            if (!tasksByDateMap.containsKey(taskDate)) {
                tasksByDateMap.put(taskDate, new java.util.ArrayList<>());
            }
            tasksByDateMap.get(taskDate).add(task);
        }

        tasksForWeek.setValue(tasksByDateMap);
    }

    public void insertNewTask(TasksTable t){
        tasksFunctionsSQL.insertTask(t);
    }

    public void updateTask(TasksTable t){
        tasksFunctionsSQL.updateTask(t);
    }

    public void deleteTask(TasksTable t){
        tasksFunctionsSQL.deleteTask(t);
    }
}