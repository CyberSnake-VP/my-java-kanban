package manager;

import tasks.Epic;
import tasks.Subtask;
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

    // методы для работы с Epic
    Epic createEpic(Epic epic);
    Epic getEpic(Integer id);
    ArrayList<Epic> getEpics();
    ArrayList<Subtask> getSubtasksListInEpic(Epic epic);
    Epic updateEpic(Epic epic);
    void deleteEpic(Integer id);
    void deleteAllEpics();

    // методы для работы с Subtask
    Subtask createSubtask(Subtask subtask);
    Subtask getSubtask(Integer id);
    ArrayList<Subtask> getSubtasks();
    Subtask updateSubtask(Subtask subtask);
    void deleteSubtask(Integer id);
    void deleteAllSubtasks();
    void deleteAllSubtasks(Epic epic);

}
