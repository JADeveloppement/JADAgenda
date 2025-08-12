package fr.jadeveloppement.agenda;

import static java.lang.Integer.parseInt;
import static java.util.Objects.isNull;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.jadeveloppement.agenda.functions.Functions;
import fr.jadeveloppement.agenda.functions.WidgetFunctions;

public class DummyTasksFunctions {
    private static final String TAG = "TaskFunctionsSQL";
    private final DummyDatabaseInstance dbFunctions;
    private final DummyTasksDAO tasksDAO;
    private final Context context;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public DummyTasksFunctions(Context context, @NotNull DummyDatabaseInstance db) {
        this.context = context;
        this.dbFunctions = db;
        this.tasksDAO = dbFunctions.tasksDAO();
    }

    public DummyTasksTable cleanTask(DummyTasksTable t){
        int[] acceptedFrequencies = {-1, 0, 1, 2, 3};

        t.label = t.label == null || t.label.isBlank() ? "" : t.label;
        t.date = t.date == null || t.date.isBlank() ? Functions.getTodayDate() : t.date;
        t.done = t.done == null ? 0 : t.done;
        t.orderNumber = t.orderNumber == null ? 0 : t.orderNumber;
        t.reminderDate = t.reminderDate == null || t.reminderDate.isEmpty() ? "" : t.reminderDate;
        t.task_ID_parent = t.task_ID_parent == null || t.task_ID_parent == 0 ? null : t.task_ID_parent;
        t.repeated = t.repeated != 0 && t.repeated != 1 ? 0 : t.repeated;
        t.repeatFrequency = t.repeatFrequency == null || t.repeatFrequency.isEmpty() || Arrays.stream(acceptedFrequencies).noneMatch(f -> f == parseInt(t.repeatFrequency)) ? "-1" : t.repeatFrequency;
        t.notification_ID = t.notification_ID == null || t.notification_ID.isEmpty() ? "" : t.notification_ID;

        return t;
    }

    private List<DummyTasksTable> updateOrderNumberOfTask(List<DummyTasksTable> tasks){
        int orderNumber = 1;
        for (int i = 0; i < tasks.size(); i++){
            DummyTasksTable task = tasks.get(i);
            if (isNull(task.task_ID_parent)){
                task.orderNumber = orderNumber;
                orderNumber++;
            }
        }
        return tasks;
    }

    public List<DummyTasksTable> getTaskOfPeriod(String date) {
        try {
            List<DummyTasksTable> tasksOfDay = executorService.submit(() -> tasksDAO.getTasksOfDay(date)).get();
            List<DummyTasksTable> tasksWithoutChildren = new ArrayList<>();
            for (DummyTasksTable t : tasksOfDay){
                if (isNull(t.task_ID_parent) || (!isNull(t.task_ID_parent) && t.repeated == 1)) tasksWithoutChildren.add(t);
            }
            return updateOrderNumberOfTask(tasksOfDay);
        } catch (Exception e) {
            handleException("getTaskOfPeriod", e);
            return Collections.emptyList();
        }
    }

    public DummyTasksTable getTask(long taskId) {
        try {
            return executorService.submit(() -> tasksDAO.getTaskById(taskId)).get();
        } catch (Exception e) {
            handleException("getTask", e);
            return new DummyTasksTable(); //
        }
    }

    public List<DummyTasksTable> getAllTasks() {
        try {
            return executorService.submit(tasksDAO::getAllTasks).get();
        } catch (Exception e) {
            handleException("getAllTasks", e);
            return Collections.emptyList();
        }
    }

    private int getNextOrderNumber(DummyTasksTable t){
        int nextOrderNumber = 1;
        List<DummyTasksTable> tasksOfDay = getTaskOfPeriod(t.date);
        for (DummyTasksTable task : tasksOfDay){
            if (isNull(task.task_ID_parent)) nextOrderNumber = task.orderNumber+1;
        }
        return nextOrderNumber;
    }

    public DummyTasksTable insertTask(DummyTasksTable t){
        try {
            t.orderNumber = getNextOrderNumber(t);
            long task_ID = executorService.submit(() -> tasksDAO.insertTask(cleanTask(t))).get();

            if (!t.repeatFrequency.isBlank()){
                int frequency = -1;
                int limit = -1;
                switch(parseInt(t.repeatFrequency)){
                    case 0:
                        frequency = 1;
                        limit = 30;
                        break;
                    case 1:
                        frequency = 7;
                        limit = 4;
                        break;
                    case 2:
                        frequency = 30;
                        limit = 12;
                        break;
                    default:
                        break;
                }

                if (frequency > -1 && limit > -1){
                    for(int repetition = 1; repetition < limit ; repetition++){
                        DummyTasksTable taskRepeat = new DummyTasksTable();
                        taskRepeat.repeated = 1;
                        taskRepeat.label = t.label;
                        String date = t.date;
                        taskRepeat.date = Functions.addXDay(date, frequency*repetition);
                        taskRepeat.task_ID_parent = task_ID;
                        executeDatabaseOperation(() -> tasksDAO.insertTask(cleanTask(taskRepeat)));
                    }
                }
            }
            WidgetFunctions.refreshWidget(context);
            return executorService.submit(() -> tasksDAO.getTaskById(task_ID)).get();
        } catch(Exception e){
            handleException("insertTask", e);
            return null;
        }
    }

    public void updateTask(DummyTasksTable t){
        try {
            WidgetFunctions.refreshWidget(context);
            executeDatabaseOperation(() -> tasksDAO.updateTask(cleanTask(t)));
        } catch(Exception e){
            handleException("updateTask", e);
        }
    }

    private void executeDatabaseOperation(Runnable databaseOperation) {
        try {
            Log.d(TAG, "executeDatabaseOperation...");
            executorService.submit(databaseOperation).get();
        } catch (Exception e) {
            handleException("Database Operation", e);
        }
    }

    private void handleException(String operation, Exception e) {
        Log.e(TAG, operation + " error: " + e.getMessage(), e); // Log with stack trace
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(context, operation + " error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    public void deleteAllTasks() {
        try {
            executeDatabaseOperation(tasksDAO::deleteAllTasks);
        } catch (Exception e){
            handleException("deleteAllTasks", e);
        }
    }

    public List<DummyTasksTable> getTasksChildren(DummyTasksTable task) {
        try {
            if (isNull(task)) return Collections.emptyList();
            else return executorService.submit(() -> tasksDAO.getChildrenTasks(task.task_ID)).get();
        } catch (Exception e){
            handleException("getTasksChildren", e);
            return Collections.emptyList();
        }
    }

    public void deleteTask(DummyTasksTable t) {
        try {
            WidgetFunctions.refreshWidget(context);
            executeDatabaseOperation(() -> {
                tasksDAO.deleteTask(t);
            });
        } catch (Exception e){
            handleException("deleteTask", e);
        }
    }
}
