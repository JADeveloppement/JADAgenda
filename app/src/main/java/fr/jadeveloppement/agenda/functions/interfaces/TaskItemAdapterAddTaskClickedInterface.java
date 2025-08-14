package fr.jadeveloppement.agenda.functions.interfaces;

import fr.jadeveloppement.agenda.functions.sqlite.tables.TasksTable;

public interface TaskItemAdapterAddTaskClickedInterface {
    void addChildrenTask(TasksTable t);
}