package fr.jadeveloppement.agenda.functions.sqlite;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import fr.jadeveloppement.agenda.functions.sqlite.dao.TasksDAO;
import fr.jadeveloppement.agenda.functions.sqlite.tables.TasksTable;

@Database(
        entities = {
                TasksTable.class,
        },
        version = 1,
        exportSchema = true
)
public abstract class DatabaseInstance extends RoomDatabase {
    public static volatile DatabaseInstance INSTANCE;

    public abstract TasksDAO tasksDAO();

    public static DatabaseInstance getInstance(Context c) {
        if (INSTANCE == null){
            synchronized (DatabaseInstance.class) {
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(
                                    c.getApplicationContext(),
                                    DatabaseInstance.class,
                                    "JADAgenda.db"
                            )
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
