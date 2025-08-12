package fr.jadeveloppement.agenda;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "dummy_tasks_table")
public class DummyTasksTable{
    @PrimaryKey(autoGenerate = true)
    public Long task_ID;

    @ColumnInfo(name = "label")
    public String label;

    @ColumnInfo(name = "date")
    public String date; // YYYY-MM-DD

    @ColumnInfo(name = "done")
    public Integer done;

    @ColumnInfo(name = "orderNumber")
    public Integer orderNumber;

    @ColumnInfo(name = "reminderDate")
    public String reminderDate; // YYYY-MM-DD HH:MM

    @ColumnInfo(name = "task_ID_parent")
    public Long task_ID_parent; // Foreign key to parent task

    @ColumnInfo(name = "repeated")
    public Integer repeated; // 0 or 1

    @ColumnInfo(name = "repeatFrequency")
    public String repeatFrequency;

    @ColumnInfo(name = "notification_ID")
    public String notification_ID;


    public DummyTasksTable(String label, String date, Integer done, Integer orderNumber,
                      String reminderDate, Long task_ID_parent, Integer repeated,
                      String repeatFrequency, String notification_ID) {
        this.label = label;
        this.date = date;
        this.done = done;
        this.orderNumber = orderNumber;
        this.reminderDate = reminderDate;
        this.task_ID_parent = task_ID_parent;
        this.repeated = repeated;
        this.repeatFrequency = repeatFrequency;
        this.notification_ID = notification_ID;
    }

    public DummyTasksTable() {
        // Default constructor for Room
    }

    // Convenience constructor for tests
    public DummyTasksTable(String label, String date) {
        this(label, date, 0, 0, "", null, 0, "-1", "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DummyTasksTable that = (DummyTasksTable) o;
        // Compare by task_ID if available, or by unique properties like label and date for new tasks
        return Objects.equals(task_ID, that.task_ID) &&
                Objects.equals(label, that.label) &&
                Objects.equals(date, that.date) &&
                Objects.equals(done, that.done) &&
                Objects.equals(orderNumber, that.orderNumber) &&
                Objects.equals(reminderDate, that.reminderDate) &&
                Objects.equals(task_ID_parent, that.task_ID_parent) &&
                Objects.equals(repeated, that.repeated) &&
                Objects.equals(repeatFrequency, that.repeatFrequency) &&
                Objects.equals(notification_ID, that.notification_ID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task_ID, label, date, done, orderNumber, reminderDate, task_ID_parent, repeated, repeatFrequency, notification_ID);
    }

    @Override
    public String toString() {
        return "TasksTable{" +
                "task_ID=" + task_ID +
                ", label='" + label + '\'' +
                ", date='" + date + '\'' +
                ", done=" + done +
                ", orderNumber=" + orderNumber +
                ", reminderDate='" + reminderDate + '\'' +
                ", task_ID_parent=" + task_ID_parent +
                ", repeated=" + repeated +
                ", repeatFrequency='" + repeatFrequency + '\'' +
                ", notification_ID='" + notification_ID + '\'' +
                '}';
    }
}