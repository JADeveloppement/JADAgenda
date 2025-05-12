package fr.jadeveloppement.agenda.functions.model;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import fr.jadeveloppement.agenda.MainActivity;
import fr.jadeveloppement.agenda.functions.Functions;
import fr.jadeveloppement.agenda.functions.sqlite.functions.TasksFunctionsSQL;
import fr.jadeveloppement.agenda.functions.sqlite.tables.TasksTable;

public class TasksViewModel extends ViewModel {
    private final Context context;
    private final TasksFunctionsSQL tasksFunctionsSQL;

    private MutableLiveData<List<TasksTable>> listOfTasks;

    public TasksViewModel(){
        this.context = MainActivity.getContext();
        this.tasksFunctionsSQL = new TasksFunctionsSQL(context);
        this.listOfTasks = new MutableLiveData<>();

        updateLiveData();
    }

    public LiveData<List<TasksTable>> getListOfTasks(){
        return listOfTasks;
    }

    private void updateLiveData() {
        List<TasksTable> allTasks = tasksFunctionsSQL.getAllTasks();

        listOfTasks.postValue(tasksFunctionsSQL.getAllTasks());
    }
}
