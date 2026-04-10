package manager;

import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class FileBackedTaskManager extends InMemoryTaskManager{

    private final File data;

    public FileBackedTaskManager(File data){
        this.data = data;
    }

    public File getData(){
        return data;
    }

    // String title = "id,type,name,description,status,epic,startTime,duration,endTime";
    // основной метод сериализации задачи в строку
    private String toString(Task task) {
        String id = Integer.toString(task.getId());
        String type = task.getType().name();
        String name = task.getName();
        String description = task.getDescription();
        String status = task.getStatus().name();
        String epicId = "";
        String startTime = task.getStartTimeToString();
        String duration = task.getDurationToString();
        String endTime = task.getEndTimeToString();
        if(type.equals(Type.SUBTASK.name())){
            epicId = Integer.toString(((Subtask) task).getEpicId());
        }
        return String.join(",", id, type, name, description, status, epicId, startTime, duration, endTime);
    }

    // метод, который собирает все задачи в один большой текст, с переносом строки для каждой задачи.
    private String getAllTasksToFile() {
        StringBuilder result = new StringBuilder();
        String title = "id,type,name,description,status,epicId,startTime,duration,endTime";
        result.append(title).append("\n");

        for (Task task : getTasks()) {
            String taskStr = toString(task);
            result.append(taskStr).append("\n");
        }
        for (Epic epic : getEpics()) {
            String epicStr = toString(epic);
            result.append(epicStr).append("\n");
        }
        for (Subtask subtask : getSubtasks()) {
            String subtaskStr = toString(subtask);
            result.append(subtaskStr).append("\n");
        }
        return result.toString();
    }

    // метод для записи в файл
    public void save() {
        String tasksToFile = getAllTasksToFile();
        try (Writer fw = new FileWriter(data)) {
            fw.write(tasksToFile);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл");
        }
    }


    @Override
    public Task createTask(Task task) {
        return super.createTask(task);
    }

    @Override
    public Task updateTask(Task task) {
        return super.updateTask(task);
    }

    @Override
    public void deleteTask(Integer id) {
        super.deleteTask(id);
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
    }

    @Override
    public Epic createEpic(Epic epic) {
        return super.createEpic(epic);
    }

    @Override
    public Epic updateEpic(Epic epic) {
        return super.updateEpic(epic);
    }

    @Override
    public void deleteEpic(Integer id) {
        super.deleteEpic(id);
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        return super.createSubtask(subtask);
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        return super.updateSubtask(subtask);
    }

    @Override
    public void deleteSubtask(Integer id) {
        super.deleteSubtask(id);
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
    }

    @Override
    public void deleteAllSubtasks(Epic epic) {
        super.deleteAllSubtasks(epic);
    }
}
