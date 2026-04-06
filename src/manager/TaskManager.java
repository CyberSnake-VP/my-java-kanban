package manager;

import tasks.Task;

import java.util.ArrayList;

public interface TaskManager {

    // методы для Task
    Task createTask(Task task);
    Task getTask(Integer id);
    Task updateTask(Task task);
    void deleteTask(Integer id);
    ArrayList<Task> getTasks();
    void deleteAllTasks();
}
