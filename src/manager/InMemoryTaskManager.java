package manager;

import exceptions.IntersectionsException;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected Integer id = 1;
    // ключ - id задачи, значение - задача
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    // список просмотренных задач(история просмотров)
    protected final HistoryManager history = Managers.getDefaultHistory();

    // используем дерево для списка приоритета задач и сортируем его по времени начала
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

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
        addInPriority(new Task(task));
        return task;
    }

    @Override
    public Task getTask(Integer id) {
        // возвращаем задачу если она существует
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            history.add(new Task(task));
            return new Task(task);
        }
        return null;
    }

    @Override
    public Task updateTask(Task task) {
        if (tasks.containsValue(task)) {
            tasks.put(task.getId(), new Task(task));
            prioritizedTasks.remove(task);
            addInPriority(task);
            return task;
        }
        return null;
    }

    @Override
    public void deleteTask(Integer id) {
        prioritizedTasks.remove(tasks.get(id));
        history.remove(id);
        tasks.remove(id);
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.values().forEach(prioritizedTasks::remove);
        tasks.keySet().forEach(history::remove);

        tasks.clear();
    }


    /// --------------------- EPICS METHODS -------------------

    @Override
    public Epic createEpic(Epic epic) {
        if (epic.getId() != null && epics.containsKey(epic.getId())) {
            return null;
        }
        Epic newEpic = new Epic(epic.getName(), epic.getDescription());
        newEpic.setId(generateId());
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    @Override
    public Epic getEpic(Integer id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            history.add(new Epic(epic));
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
            Epic epicToUpdate = epics.get(epic.getId());
            if(epicToUpdate != null) {
                epicToUpdate.setName(epic.getName());
                epicToUpdate.setDescription(epic.getDescription());
                // получение списка подзадач эпика один раз и использование несколько раз в последующем
                ArrayList<Subtask> subtaskList = getSubtasksListInEpic(epic);
                // проверка статуса эпика
                EpicSettings.setStatus(epicToUpdate, subtaskList);
                // проверка времени эпика
                EpicSettings.setEpicTime(epicToUpdate, subtaskList);
                return new Epic(epicToUpdate);
            }
        return null;
    }

    @Override
    public void deleteEpic(Integer id) {
            Epic epic = epics.get(id);
            if(epic != null) {
                // удаляю все подзадачи из списка приоритета
                getSubtasksListInEpic(epic).forEach(subtask -> {
                    prioritizedTasks.remove(subtask);
                    subtasks.remove(subtask.getId());
                    history.remove(subtask.getId());
                });
                history.remove(epic.getId());
                epics.remove(id);
            }
    }

    @Override
    public void deleteAllEpics() {
        epics.values().forEach(epic -> {
           epic.getSubtaskIdList().forEach(subId->{
               history.remove(subId);
               prioritizedTasks.remove(subtasks.get(subId));
           });
           history.remove(epic.getId());
        });
        epics.clear();
        subtasks.clear();
    }


    /// ------------------------ SUBTASKS METHODS ----------------------------------

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId()) || !epics.containsKey(subtask.getEpicId())) {
            return null;
        }

        subtask.setId(generateId());
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskIdList(subtask.getId());
        subtasks.put(subtask.getId(), new Subtask(subtask));
        addInPriority(new Subtask(subtask)); // добавляю в список приоритета
        ArrayList<Subtask> subList = getSubtasksListInEpic(epic);
        EpicSettings.setStatus(epic, subList);
        EpicSettings.setEpicTime(epic, subList);
        return subtask;
    }

    @Override
    public Subtask getSubtask(Integer id) {
            Subtask subtask = subtasks.get(id);
            if (subtask != null) {
                history.add(new Subtask(subtask));
                return new Subtask(subtask);
            }
        return null;
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
            Epic epic = epics.get(subtask.getEpicId());
            if(epic != null) {
                // беру старую подзадачу
                Subtask oldSubtask = subtasks.get(subtask.getId());
                // удаляю ее
                prioritizedTasks.remove(oldSubtask);
                // добавляю новую подзадачу
                subtasks.put(subtask.getId(), new Subtask(subtask));
                // записываю ее в список приоритета
                addInPriority(subtask);
                // один раз обращаюсь к map получая список подзадач у эпика
                ArrayList<Subtask> subList = getSubtasksListInEpic(epic);
                EpicSettings.setEpicTime(epic, subList); // устанавливаем время
                EpicSettings.setStatus(epic, subList); // устанавливаю новый статус
                return subtask;
            }
        return null;
    }

    @Override
    public void deleteSubtask(Integer id) {
        //находим подзадачу
        Subtask subtask = subtasks.get(id);
        if(subtask != null) {
            // получаем ее эпик и удаляем подзадачу из его списка
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskIdList().remove(id);
            // получаем обновленный список подзадач у эпика
            ArrayList<Subtask> subList = getSubtasksListInEpic(epic);
            EpicSettings.setStatus(epic, subList);
            EpicSettings.setEpicTime(epic, subList);
            // удаляем подзадачу из списка приоритета, истории, хранилища
            prioritizedTasks.remove(subtask);
            history.remove(id);
            subtasks.remove(id);
        }
    }

    @Override
    public void deleteAllSubtasks() {
        ArrayList<Integer> subtaskIdList = new ArrayList<>();
        // удаляю из списков у эпиков все подзадачи и записываю их в список
        epics.values().forEach(epic -> {
            subtaskIdList.addAll(epic.getSubtaskIdList());
            epic.getSubtaskIdList().clear();
            epic.setStatus(Status.NEW);
        });
        // использую список для удаления из истории по id
        subtaskIdList.forEach(id-> {
            history.remove(id);
            Subtask subtask = subtasks.get(id);
            if(subtask != null) {
                prioritizedTasks.remove(subtask);
            }
        });
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks(Epic epic) {
            Epic epicToDelete = epics.get(epic.getId());
            if(epicToDelete != null) {
                ArrayList<Integer> subtaskIdList = epicToDelete.getSubtaskIdList();
                subtaskIdList.forEach(subId -> {
                    // удаляю из истории
                    history.remove(subId);
                    // удаляю из списка приоритета
                    Subtask subtask = subtasks.get(subId);
                    if(subtask != null) {
                        prioritizedTasks.remove(subtask);
                    }
                    // удаляю подзадачу из хранилища
                    subtasks.remove(subId);
                });
                epicToDelete.getSubtaskIdList().clear();
                epicToDelete.setStatus(Status.NEW);
            }
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }


    @Override
    public List<Task> getPrioritized() {
        return new ArrayList<>(prioritizedTasks);
    }

    // добавляем задачу в список приоритета только с указанным временем начала и продолжительности
    protected void addInPriority(Task task) {
        // у задачи должны быть заданы время начала и продолжительность
        Instant startTime = task.getStartTime();
        Duration duration = task.getDuration();

        // добавлять в список приоритета в том случае, если пройдена валидация
        if (startTime != null && duration != null) {
            if(isValidIntersection(task)) {
                prioritizedTasks.add(task);
                return;
            }
            // валидация не пройдена выбрасываем исключение
            throw new IntersectionsException("Task is not in prioritized list");
        }
    }

    private boolean isValidIntersection(Task task) {
        if (getPrioritized().isEmpty()) {
            return true;
        }
        // startTime <= endTime(в списке) && endTime >= startTime( в списке)
        return getPrioritized().stream()
                .noneMatch(t-> task.getStartTime().isBefore(t.getEndTime())
                                && task.getEndTime().isAfter(t.getStartTime())
                        );
    }

}
