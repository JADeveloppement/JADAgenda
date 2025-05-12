package fr.jadeveloppement.agenda.functions.sqlite.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import fr.jadeveloppement.agenda.functions.sqlite.tables.TasksTable;

@Dao
public interface TasksDAO {
    @Insert
    Long insertTask(TasksTable task);

    @Query("SELECT * FROM tasks WHERE task_ID = :task_ID")
    TasksTable getTaskById(long task_ID);

    @Query("SELECT * FROM tasks WHERE task_ID_parent = :task_ID")
    List<TasksTable> getChildrenTasks(long task_ID);

    @Query("SELECT * FROM tasks")
    List<TasksTable> getAllTasks();

    @Query("SELECT * FROM tasks WHERE date BETWEEN :startDate AND :endDate ORDER BY task_ID ASC, orderNumber ASC, done ASC")
    List<TasksTable> getTaskFromWeek(String startDate, String endDate);

    @Query("SELECT * FROM tasks WHERE date = :date AND (task_ID_parent IS NULL OR repeated = 1) ORDER BY done ASC, orderNumber ASC")
    List<TasksTable> getTasksOfDay(String date);

    @Update
    void updateTask(TasksTable task);

    @Delete
    void deleteTask(TasksTable task);

    @Query("DELETE FROM tasks WHERE task_ID IN (:id)")
    void deleteTasksById(List<Long> id);

    @Query("DELETE FROM tasks")
    void deleteAllTasks();

    @Transaction
    default void updateTasksInTransaction(List<TasksTable> tasks){
        for(TasksTable t : tasks)
            updateTask(t);
    }

    @Query("SELECT * FROM 'tasks' WHERE task_ID_parent = :id LIMIT 1")
    List<TasksTable> taskChildren(long id);

    @Query("SELECT * FROM 'tasks' WHERE repeated = 1 AND task_ID_parent = :id")
    List<TasksTable> getRepeatedTasks(long id);
}
