package manager;

import exceptions.ManagerSaveException;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

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
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Task updateTask(Task task) {
       super.updateTask(task);
       save();
       return task;
    }

    @Override
    public void deleteTask(Integer id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Epic createEpic(Epic epic) {
         super.createEpic(epic);
         save();
         return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
         super.updateEpic(epic);
         save();
         return epic;
    }

    @Override
    public void deleteEpic(Integer id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
         super.createSubtask(subtask);
         save();
         return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
         super.updateSubtask(subtask);
         save();
         return subtask;
    }

    @Override
    public void deleteSubtask(Integer id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllSubtasks(Epic epic) {
        super.deleteAllSubtasks(epic);
        save();
    }

    public static void main(String[] args) {
//        System.out.println(System.getProperty("user.dir"));
        TaskManager fm = new FileBackedTaskManager(new File("src/files/data.csv"));
        Task task1 = new Task("Задача1", "Описание",
                LocalDateTime.of(2025, 1, 1, 11, 0, 0)
                        .toInstant(ZoneOffset.UTC), Duration.ofMinutes(60));

        Task task2 = new Task("Задача2", "Описание", Status.IN_PROGRESS,
                LocalDateTime.of(2025, 1, 1, 12, 0)
                        .toInstant(ZoneOffset.UTC), Duration.ofMinutes(60));

        fm.createTask(task1);
        fm.createTask(task2);

        Epic epic1 = new Epic("Эпик1", "Действие");
        Epic epic2 = new Epic("Эпик2", "Действие");
        fm.createEpic(epic1);
        fm.createEpic(epic2);


        Subtask subtask1 = new Subtask("Подзадача1", "Эпик1", Status.DONE,
                LocalDateTime.of(2025, 1, 1, 13, 50)
                        .toInstant(ZoneOffset.UTC), Duration.ofMinutes(60), epic1);
        Subtask subtask2 = new Subtask("Подзадача2", "Эпик1",
                LocalDateTime.of(2025, 1, 1, 14, 50)
                        .toInstant(ZoneOffset.UTC), Duration.ofMinutes(60), epic1);

        // подзадача пересечется
        Subtask subtask3 = new Subtask("Подзадача3", "Эпик2",
                LocalDateTime.of(2025, 1, 1, 10, 0)
                        .toInstant(ZoneOffset.UTC), Duration.ofMinutes(65), epic2);
        fm.createSubtask(subtask1);
        fm.createSubtask(subtask2);
        fm.createSubtask(subtask3);

        System.out.println("ПРИОРИТЕТ ВЫПОЛНЕНИЯ ЗАДАЧ:");
        for (Task task : fm.getPrioritized()) {
            System.out.printf("%-10S | %-8s | статус: %-12S | id%-2d | старт: %-15s | %-3s минут | завершение: %-15s %n",
                    task.getName(), task.getDescription(), task.getStatus(), task.getId(), task.getStartTimeToString(),
                    task.getDurationToString(), task.getEndTimeToString());
        }
        
    }
}
