package manager;

import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    protected Integer id = 1;
    // ключ - id задачи, значение - задача
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private Integer generateId() {
        return id++;
    }

    @Override
    public Task createTask(Task task) {
        // проверим наличие задачи
        if (tasks.containsValue(task)) {
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
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            return new Task(task);
        }
        return null;
    }

    @Override
    public Task updateTask(Task task) {
        if (tasks.containsValue(task)) {
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


    /// --------------------- EPICS METHODS -------------------

    @Override
    public Epic createEpic(Epic epic) {
        if (epics.containsValue(epic)) {
            return null;
        }
        epic.setId(generateId());
        epics.put(epic.getId(), new Epic(epic));
        return epic;
    }

    @Override
    public Epic getEpic(Integer id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            return new Epic(epic);
        }
        return null;
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasksListInEpic(Epic epic) {
        if (epics.containsValue(epic)) {
            ArrayList<Subtask> subtaskList = new ArrayList<>();

            // получаю список id подзадач эпика и пробегаюсь по нему получая по id задачу
            // и добавляю ее в список
            for (Integer id : epic.getSubtaskIdList()) {
                subtaskList.add(subtasks.get(id));
            }
            return subtaskList;
        }
        return null;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epics.containsValue(epic)) {
            Epic epicToUpdate = epics.get(epic.getId());
            epicToUpdate.setName(epic.getName());
            epicToUpdate.setDescription(epic.getDescription());
            epicToUpdate.setSubtaskIdList(epic.getSubtaskIdList());
            // проверка статуса эпика
            EpicSettings.setStatus(epicToUpdate, getSubtasksListInEpic(epic));
            return new Epic(epicToUpdate);
        }
        return null;
    }

    @Override
    public void deleteEpic(Integer id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            // удаляем по id его подзадач
            epic.getSubtaskIdList().forEach(subtasks::remove);
            epics.remove(id);
        }
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }


    /// ------------------------ SUBTASKS METHODS ----------------------------------

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtasks.containsValue(subtask) || !epics.containsKey(subtask.getEpicId())) {
            return null;
        }

        subtask.setId(generateId());
        Epic epic = epics.get(subtask.getEpicId());
        epic.setSubtaskIdList(subtask.getId());
        subtasks.put(subtask.getId(), new Subtask(subtask));
        EpicSettings.setStatus(epic, getSubtasksListInEpic(epic));
        return subtask;
    }

    @Override
    public Subtask getSubtask(Integer id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            return new Subtask(subtask);
        }
        return null;
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<Subtask>(subtasks.values());
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (subtasks.containsValue(subtask)) {
            Epic epic = epics.get(subtask.getEpicId());
            subtasks.put(subtask.getId(), new Subtask(subtask));
            EpicSettings.setStatus(epic, getSubtasksListInEpic(epic));
            return subtask;
        }
        return null;
    }

    @Override
    public void deleteSubtask(Integer id) {
        //находим подзадачу
        Subtask subtask = subtasks.get(id);
        // получаем ее эпик и удаляем подзадачу из его списка
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtaskIdList().remove(id);

        EpicSettings.setStatus(epic, getSubtasksListInEpic(epic));
        subtasks.remove(id);
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtaskIdList().clear();
            epic.setStatus(Status.NEW);
        });
    }

    @Override
    public void deleteAllSubtasks(Epic epic) {
        if(epics.containsValue(epic)) {
            Epic epicToDelete = epics.get(epic.getId());
            epicToDelete.getSubtaskIdList().clear();
            epicToDelete.setStatus(Status.NEW);

            ArrayList<Integer> subtaskIdList = epic.getSubtaskIdList();
            subtasks.keySet().stream().filter(subtaskIdList::contains).forEach(subtasks::remove);
        }
    }

}
