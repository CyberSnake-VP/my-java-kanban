import manager.InMemoryTaskManager;
import manager.TaskManager;
import tasks.Task;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("1", "description", Instant.now(), Duration.ofMinutes(10));
        Task task2 = new Task("2", "description", Instant.now(), Duration.ofMinutes(10));
        task1 = manager.createTask(task1);
        task2 = manager.createTask(task2);
        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.getTask(task1.getId());
        List<Task> tasks = manager.getHistory();
        System.out.println(tasks);
    }
}
