package fr.jadeveloppement.agenda.functions.sqlite.tables;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks",
        foreignKeys = @ForeignKey(
                entity = TasksTable.class,
                parentColumns = "task_ID",
                childColumns = "task_ID_parent",
                onDelete = ForeignKey.SET_NULL,
                onUpdate = ForeignKey.CASCADE
        ))
public class TasksTable {
    @PrimaryKey(autoGenerate = true)
    public long task_ID;

    public String label;
    public String date;
    public Integer done;
    public Integer orderNumber;
    public String reminderDate;

    public Long task_ID_parent;

    public int repeated;

    public String repeatFrequency;

    public String notification_ID;
}
