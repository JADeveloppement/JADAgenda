package fr.jadeveloppement.agenda.functions.interfaces;

import androidx.annotation.NonNull;

import fr.jadeveloppement.agenda.functions.sqlite.tables.TasksTable;

public interface TaskItemAdapterDeleteTaskClickedInterface {
    void taskAdapterDeleteTaskClicked(TasksTable tasksTable);
}
