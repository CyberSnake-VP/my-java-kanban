package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager{
   protected Integer id = 1;
   // ключ - id задачи, значение - задача
   protected final HashMap<Integer, Task> tasks = new HashMap<>();
   protected final HashMap<Integer, Epic> epics = new HashMap<>();
   protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private Integer generateId(){
        return id++;
    }

    @Override
    public Task createTask(Task task) {
        // проверим наличие задачи
        if(tasks.containsValue(task)){
            return null;
        }
        // устанавливаем id
        task.setId(generateId());

        // записываем задачу
        tasks.put(task.getId(), new Task(task));
        return task;
    }

    @Override
    public Task getTask(Integer id) {
        // возвращаем задачу если она существует
        if(tasks.containsKey(id)){
            Task task = tasks.get(id);
            return new Task(task);
        }
        return null;
    }

    @Override
    public Task updateTask(Task task) {
        if(tasks.containsValue(task)){
            tasks.put(task.getId(), new Task(task));
            return task;
        }
        return null;
    }

    @Override
    public void deleteTask(Integer id) {
        tasks.remove(id);
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }
}
