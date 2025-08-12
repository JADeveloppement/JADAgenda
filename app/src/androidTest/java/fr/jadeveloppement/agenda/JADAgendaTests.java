package fr.jadeveloppement.agenda;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import fr.jadeveloppement.agenda.functions.Functions;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class JADAgendaTests {
    private DummyTasksFunctions tasksFunctionsSQL;
    private DummyDatabaseInstance db;
    private Context context;

    @Before
    public void createDb() {
        try {
            context = InstrumentationRegistry.getInstrumentation().getTargetContext();

            db = Room.inMemoryDatabaseBuilder(
                            context.getApplicationContext(),
                            DummyDatabaseInstance.class
                    )
                    .allowMainThreadQueries()
                    .build();
            if (db == null) {
                System.err.println("ERROR: DummyDatabaseInstance.getInstance(context) returned NULL.");
                throw new IllegalStateException("Database instance is NULL. Cannot proceed with test setup.");
            }
            System.out.println("DEBUG: Database instance created: " + (db != null ? db.getClass().getSimpleName() : "null"));

            DummyTasksDAO tasksDAO = db.tasksDAO();
            if (tasksDAO == null) {
                System.err.println("ERROR: tasksDAO is NULL after db.tasksDAO(). This indicates a problem with Room setup.");
                throw new IllegalStateException("TasksDAO is NULL. Cannot proceed with test setup.");
            }
            System.out.println("DEBUG: TasksDAO obtained.");

            tasksFunctionsSQL = new DummyTasksFunctions(context, db);
            System.out.println("DEBUG: DummyTasksFunctions instance created.");

        } catch (Exception e) {
            System.err.println("FATAL ERROR in @Before createDb(): " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Test setup failed due to: " + e.getMessage(), e);
        }
    }

    @After
    public void closeDb() throws IOException {
        System.out.println("DEBUG: @After closeDb() called.");
        if (db != null) {
            db.close();
            System.out.println("DEBUG: Database closed successfully.");
        } else {
            System.err.println("WARNING: Attempted to close a NULL database in @After. This confirms a setup failure.");
        }
    }

    @Test
    public void testCleanTask() {
        DummyTasksTable task = new DummyTasksTable();
        task.label = null;
        task.date = "";
        task.done = null;
        task.orderNumber = null;
        task.reminderDate = "";
        task.task_ID_parent = 0L; // 0 should become null
        task.repeated = 5; // Invalid value
        task.repeatFrequency = "-12"; // Invalid value
        task.notification_ID = null;

        DummyTasksTable cleanedTask = tasksFunctionsSQL.cleanTask(task);

        assertEquals("Label should be empty string", "", cleanedTask.label);
        assertEquals("Date should be today's date", Functions.getTodayDate(), cleanedTask.date);
        assertEquals("Done should be 0", Integer.valueOf(0), cleanedTask.done);
        assertEquals("OrderNumber should be 0", Integer.valueOf(0), cleanedTask.orderNumber);
        assertEquals("ReminderDate should be empty string", "", cleanedTask.reminderDate);
        assertNull("Task_ID_parent should be null", cleanedTask.task_ID_parent);
        assertEquals("Repeated should be 0", Integer.valueOf(0), cleanedTask.repeated);
        assertEquals("RepeatFrequency should be -1 V2", "-1", cleanedTask.repeatFrequency);
        assertEquals("RepeatFrequency should be -1", "-1", cleanedTask.repeatFrequency);
        assertEquals("Notification_ID should be empty string", "", cleanedTask.notification_ID);

        DummyTasksTable validTask = new DummyTasksTable("My Valid Task", "2025-08-20");
        validTask.task_ID_parent = 1L;
        validTask.repeated = 1;
        validTask.repeatFrequency = "0"; // Daily

        DummyTasksTable cleanedValidTask = tasksFunctionsSQL.cleanTask(validTask);
        assertEquals("My Valid Task", cleanedValidTask.label);
        assertEquals("2025-08-20", cleanedValidTask.date);
        assertEquals(Integer.valueOf(1), cleanedValidTask.repeated);
        assertEquals("0", cleanedValidTask.repeatFrequency);
        assertEquals((Long) 1L, cleanedValidTask.task_ID_parent);
    }

    @Test
    public void testInsertAndGetTask() {
        DummyTasksTable newTask = new DummyTasksTable("Buy groceries", "2025-08-15");

        DummyTasksTable insertedTask = tasksFunctionsSQL.insertTask(newTask);

        assertNotNull("Inserted task should not be null", insertedTask);
        assertNotNull("Inserted task ID should not be null", insertedTask.task_ID);
        assertTrue("Task ID should be positive", insertedTask.task_ID > 0);
        assertEquals("Label should match", "Buy groceries", insertedTask.label);
        assertEquals("Date should match", "2025-08-15", insertedTask.date);
        assertEquals("Order number should be 1", (Integer) 1, insertedTask.orderNumber);

        DummyTasksTable retrievedTask = tasksFunctionsSQL.getTask(insertedTask.task_ID);
        assertNotNull("Retrieved task should not be null", retrievedTask);
        assertEquals("Retrieved task should match inserted task", insertedTask, retrievedTask);
    }

    @Test
    public void testUpdateTask() {
        DummyTasksTable newTask = new DummyTasksTable("Old Label", "2025-08-16");
        DummyTasksTable insertedTask = tasksFunctionsSQL.insertTask(newTask);

        assertNotNull(insertedTask);

        insertedTask.label = "New Label";
        insertedTask.done = 1;
        tasksFunctionsSQL.updateTask(insertedTask);

        DummyTasksTable updatedTask = tasksFunctionsSQL.getTask(insertedTask.task_ID);
        assertNotNull(updatedTask);
        assertEquals("Label should be updated", "New Label", updatedTask.label);
        assertEquals("Done status should be updated", (Integer) 1, updatedTask.done);
    }

    @Test
    public void testDeleteTask() {
        DummyTasksTable newTask = new DummyTasksTable("Task to delete", "2025-08-17");
        DummyTasksTable insertedTask = tasksFunctionsSQL.insertTask(newTask);

        assertNotNull(insertedTask);
        tasksFunctionsSQL.deleteTask(insertedTask);

        DummyTasksTable deletedTask = tasksFunctionsSQL.getTask(insertedTask.task_ID);
        assertNull("Task should be null after deletion", deletedTask);
    }

    @Test
    public void testGetAllTasks() {
        tasksFunctionsSQL.insertTask(new DummyTasksTable("Task 1", "2025-08-18"));
        tasksFunctionsSQL.insertTask(new DummyTasksTable("Task 2", "2025-08-19"));
        List<DummyTasksTable> allTasks = tasksFunctionsSQL.getAllTasks();
        assertEquals("Should retrieve 2 tasks", 2, allTasks.size());
    }

    @Test
    public void testGetTaskOfPeriod() {
        String testDate = "2025-09-01";
        tasksFunctionsSQL.insertTask(new DummyTasksTable("Task for Sep 1", testDate));
        tasksFunctionsSQL.insertTask(new DummyTasksTable("Another for Sep 1", testDate));
        tasksFunctionsSQL.insertTask(new DummyTasksTable("Task for Sep 2", "2025-09-02"));

        List<DummyTasksTable> tasksOnSept1 = tasksFunctionsSQL.getTaskOfPeriod(testDate);
        assertEquals("Should retrieve 2 tasks for 2025-09-01", 2, tasksOnSept1.size());
        for (DummyTasksTable t : tasksOnSept1) {
            assertEquals("All retrieved tasks should be for 2025-09-01", testDate, t.date);
        }
        assertEquals("Order number for first task should be 1", (Integer)1, tasksOnSept1.get(0).orderNumber);
        assertEquals("Order number for second task should be 2", (Integer)2, tasksOnSept1.get(1).orderNumber);
    }

    @Test
    public void testGetTasksChildren() {
        DummyTasksTable parentTask = new DummyTasksTable("Parent Task", "2025-08-20");
        DummyTasksTable insertedParent = tasksFunctionsSQL.insertTask(parentTask);

        DummyTasksTable child1 = new DummyTasksTable("Child 1", "2025-08-20");
        child1.task_ID_parent = insertedParent.task_ID;
        tasksFunctionsSQL.insertTask(child1);

        DummyTasksTable child2 = new DummyTasksTable("Child 2", "2025-08-20");
        child2.task_ID_parent = insertedParent.task_ID;
        tasksFunctionsSQL.insertTask(child2);

        List<DummyTasksTable> children = tasksFunctionsSQL.getTasksChildren(insertedParent);
        assertEquals("Should retrieve 2 children tasks", 2, children.size());
        assertEquals("First child label should be Child 1", "Child 1", children.get(0).label);
        assertEquals("Second child label should be Child 2", "Child 2", children.get(1).label);
        assertEquals("All children should reference parent ID", (Long) insertedParent.task_ID, children.get(0).task_ID_parent);
    }

    @Test
    public void testDeleteAllTasks() {
        tasksFunctionsSQL.insertTask(new DummyTasksTable("Task A", "2025-08-21"));
        tasksFunctionsSQL.insertTask(new DummyTasksTable("Task B", "2025-08-22"));
        assertEquals(2, tasksFunctionsSQL.getAllTasks().size());

        tasksFunctionsSQL.deleteAllTasks();
        assertEquals("All tasks should be deleted", 0, tasksFunctionsSQL.getAllTasks().size());
    }

    @Test
    public void testRepeatedTaskCreation() {
        DummyTasksTable repeatDailyTask = new DummyTasksTable("Repeat Daily", "2025-08-25");
        repeatDailyTask.repeated = 0;
        repeatDailyTask.repeatFrequency = "0";

        DummyTasksTable insertedParent = tasksFunctionsSQL.insertTask(repeatDailyTask);
        assertNotNull(insertedParent);
        assertNotNull(insertedParent.task_ID);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<DummyTasksTable> allTasks = tasksFunctionsSQL.getAllTasks();
        Log.d("JADAgendaTests", "testRepeatedTaskCreation: size : " + allTasks.size());
        // Expect parent + 30 children (limit = 30 for daily)
        assertEquals("Should have parent + 29 repeated tasks", 30, allTasks.size());

        // Verify some children properties
        List<DummyTasksTable> children = tasksFunctionsSQL.getTasksChildren(insertedParent);
        assertEquals("Should have 29 children for daily task", 29, children.size());
        assertEquals("First child should be for tomorrow", "2025-08-26", children.get(0).date);
        assertEquals("Second child should be for day after tomorrow", "2025-08-27", children.get(1).date);
        assertEquals("Last child should be for 30 days later", "2025-09-23", children.get(children.size()-1).date); // 25 + 30 -1

        for (DummyTasksTable child : children) {
            assertEquals("Children should reference parent ID", (Long) insertedParent.task_ID, child.task_ID_parent);
            assertEquals("Children should be marked as repeated", (Integer) 1, child.repeated);
            assertEquals("Children label should match parent", insertedParent.label, child.label);
        }
    }

    @Test
    public void testOrderNumberAssignment() {
        String date = "2025-09-05";
        DummyTasksTable task1 = new DummyTasksTable("Task One", date);
        DummyTasksTable inserted1 = tasksFunctionsSQL.insertTask(task1);
        assertEquals("Order number for first task should be 1", (Integer) 1, inserted1.orderNumber);

        DummyTasksTable task2 = new DummyTasksTable("Task Two", date);
        DummyTasksTable inserted2 = tasksFunctionsSQL.insertTask(task2);
        assertEquals("Order number for second task should be 2", (Integer) 2, inserted2.orderNumber);

        DummyTasksTable task3Child = new DummyTasksTable("Task Three (child)", date);
        task3Child.task_ID_parent = inserted1.task_ID;
        DummyTasksTable inserted3 = tasksFunctionsSQL.insertTask(task3Child);
        assertEquals("Order number of child task should be 3", (Integer) 3, inserted3.orderNumber); // CleanTask sets it to 0 if null initially.

        // Re-fetch to ensure order numbers are persisted
        List<DummyTasksTable> tasks = tasksFunctionsSQL.getTaskOfPeriod(date);
        assertEquals(3, tasks.size());
        DummyTasksTable retrievedTask1 = tasks.get(0);
        DummyTasksTable retrievedTask2 = tasks.get(1);

        assertEquals("Order number of first non-child task should be 1", (Integer) 1, retrievedTask1.orderNumber);
        assertEquals("Order number of second non-child task should be 2", (Integer) 2, retrievedTask2.orderNumber);
    }
}