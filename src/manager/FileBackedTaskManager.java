package manager;

import exceptions.ManagerBackupException;
import exceptions.ManagerSaveException;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File data;

    public FileBackedTaskManager(File data) {
        this.data = data;
    }

    public File getData() {
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
        if (type.equals(Type.SUBTASK.name())) {
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

    // десиреализация задачи из строки
    private Task fromString(String value) {
        String[] split = value.split(",");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy|HH:mm");
        int id = Integer.parseInt(split[0]);
        String type = split[1];
        String name = split[2];
        String description = split[3];
        Status status = Status.valueOf(split[4]);
        int epicId = (split[5].isBlank() ? 0 : Integer.parseInt(split[5]));
        Instant startTime = (split[6].equals("null") ? null :
                LocalDateTime.parse(split[6], formatter).toInstant(ZoneOffset.UTC));
        Duration duration = (split[7].equals("null") ? null :
                Duration.ofMinutes(Integer.parseInt(split[7])));
        Instant endTime = (split[8].equals("null") ? null :
                LocalDateTime.parse(split[8], formatter).toInstant(ZoneOffset.UTC));
        // поле endTime не нужно высчитывать потому, что оно само высчитывается при создании задач

        switch (type) {
            case "TASK":
                Task task = new Task(name, description, status, startTime, duration);
                task.setId(id);
                return task;
            case "EPIC":
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                epic.setStartTime(startTime);
                epic.setDuration(duration);
                epic.setEndTime(endTime);
                return epic;
            case "SUBTASK":
                Epic epicForSubtask = epics.get(epicId);
                Subtask subtask = new Subtask(name, description, status, startTime, duration, epicForSubtask);
                subtask.setId(id);
                epicForSubtask.addSubtaskIdList(id);
                return subtask;
        }
        return null;
    }



    // заполняем хранилище
    private void putToMaps(Task task) {
        Type type = task.getType();
        int id = task.getId();
        switch (type) {
            case TASK -> tasks.put(id, task);
            case EPIC -> epics.put(id, (Epic)task);
            case SUBTASK -> subtasks.put(id, (Subtask)task);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try {
            // счетчик id задач, чтобы установить значение id равное последней задачи
            int count = 0;

            FileBackedTaskManager manager = new FileBackedTaskManager(file);
            List<String> lines = Files.readAllLines(file.toPath());
            if(lines.isEmpty()) {
                return manager;
            }
            for (String line : lines) {
                if(!Character.isDigit(line.charAt(0))) {
                    continue;
                }
                Task task = manager.fromString(line);
                manager.putToMaps(task);


                // обновляем наш счетчик id
                if (count < task.getId()){
                    count = task.getId();
                }

            }

            manager.setNextId(count);

            return manager;
        } catch (IOException e) {
            throw new ManagerBackupException("Ошибка при загрузки из файла");
        }
    }

    private void setNextId(int count) {
        id = count + 1;
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
//        TaskManager fm = new FileBackedTaskManager(new File("src/files/data.csv"));
//        Task task1 = new Task("Задача1", "Описание",
//                LocalDateTime.of(2025, 1, 1, 11, 0, 0)
//                        .toInstant(ZoneOffset.UTC), Duration.ofMinutes(60));
//
//        Task task2 = new Task("Задача2", "Описание", Status.IN_PROGRESS,
//                LocalDateTime.of(2025, 1, 1, 12, 0)
//                        .toInstant(ZoneOffset.UTC), Duration.ofMinutes(60));
//
//        fm.createTask(task1);
//        fm.createTask(task2);
//
//        Epic epic1 = new Epic("Эпик1", "Действие");
//        Epic epic2 = new Epic("Эпик2", "Действие");
//        fm.createEpic(epic1);
//        fm.createEpic(epic2);
//
//
//        Subtask subtask1 = new Subtask("Подзадача1", "Эпик1", Status.DONE,
//                LocalDateTime.of(2025, 1, 1, 13, 50)
//                        .toInstant(ZoneOffset.UTC), Duration.ofMinutes(60), epic1);
//        Subtask subtask2 = new Subtask("Подзадача2", "Эпик1",
//                LocalDateTime.of(2025, 1, 1, 14, 50)
//                        .toInstant(ZoneOffset.UTC), Duration.ofMinutes(60), epic1);
//
//        // подзадача пересечется
//        Subtask subtask3 = new Subtask("Подзадача3", "Эпик2",
//                LocalDateTime.of(2025, 1, 1, 10, 0)
//                        .toInstant(ZoneOffset.UTC), Duration.ofMinutes(65), epic2);
//        fm.createSubtask(subtask1);
//        fm.createSubtask(subtask2);
//        fm.createSubtask(subtask3);
//
//        System.out.println("ПРИОРИТЕТ ВЫПОЛНЕНИЯ ЗАДАЧ:");
//        for (Task task : fm.getPrioritized()) {
//            System.out.printf("%-10S | %-8s | статус: %-12S | id%-2d | старт: %-15s | %-3s минут | завершение: %-15s %n",
//                    task.getName(), task.getDescription(), task.getStatus(), task.getId(), task.getStartTimeToString(),
//                    task.getDurationToString(), task.getEndTimeToString());
//        }
        TaskManager fm = Managers.loadFromFile(new File("src/files/data.csv"));
        printTaskTest(fm);
    }

    static void printTaskTest(TaskManager fbm) {
        fbm.getTasks().forEach(task -> {
            System.out.printf("%-10S | %-8s | статус: %-12S | id%-2d | старт: %-15S |  %-3s минут | завершение: %-15s \n",
                    task.getName(), task.getDescription(), task.getStatus().name(), task.getId(),
                    task.getStartTimeToString(), task.getDurationToString(), task.getEndTimeToString());
        });

        System.out.println();

        fbm.getEpics().forEach(epic -> {
            System.out.printf("%-10S | %-8s | статус: %-12S | id%-2d | старт: %-15S |  %-3s минут | завершение: %-15s \n",
                    epic.getName(), epic.getDescription(), epic.getStatus().name(), epic.getId(),
                    epic.getStartTimeToString(), epic.getDurationToString(), epic.getEndTimeToString());
        });

        System.out.println();

        fbm.getSubtasks().forEach(subtask -> {
            System.out.printf("%-10S | %-8s | статус: %-12S | id%-2d | старт: %-15S |  %-3s минут | завершение: %-15s \n",
                    subtask.getName(), subtask.getDescription(), subtask.getStatus().name(), subtask.getId(),
                    subtask.getStartTimeToString(), subtask.getDurationToString(), subtask.getEndTimeToString());
        });
        System.out.println("\n");
    }
}
