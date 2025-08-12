package fr.jadeveloppement.agenda;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DummyTasksDAO {
    @Insert
    long insertTask(DummyTasksTable task);

    @Update
    void updateTask(DummyTasksTable task);

    @Delete
    void deleteTask(DummyTasksTable task);

    @Query("SELECT * FROM dummy_tasks_table WHERE date = :date ORDER BY orderNumber ASC")
    List<DummyTasksTable> getTasksOfDay(String date);

    @Query("SELECT * FROM dummy_tasks_table WHERE task_ID = :taskId")
    DummyTasksTable getTaskById(long taskId);

    @Query("SELECT * FROM dummy_tasks_table ORDER BY date ASC, orderNumber ASC")
    List<DummyTasksTable> getAllTasks();

    @Query("DELETE FROM dummy_tasks_table")
    void deleteAllTasks();

    @Query("SELECT * FROM dummy_tasks_table WHERE task_ID_parent = :parentId ORDER BY date ASC, orderNumber ASC")
    List<DummyTasksTable> getChildrenTasks(long parentId);
}
