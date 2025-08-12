package fr.jadeveloppement.agenda;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {DummyTasksTable.class}, version = 1, exportSchema = false)
public abstract class DummyDatabaseInstance extends RoomDatabase {
    public abstract DummyTasksDAO tasksDAO();

    public static DummyDatabaseInstance getInstance(Context c) {
        return Room.inMemoryDatabaseBuilder(
                        c.getApplicationContext(), // Use application context for database builders
                        DummyDatabaseInstance.class
                )
                .allowMainThreadQueries() // Allow queries on the main thread for simplicity in tests
                .build();
    }

}
