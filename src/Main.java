import manager.InMemoryTaskManager;
import manager.TaskManager;
import tasks.Task;

import java.time.Duration;
import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new InMemoryTaskManager();
        Task task = new Task("name", "description", Instant.now(), Duration.ofMinutes(10));


        Task taskWithId = manager.createTask(task);
        task = manager.getTask(taskWithId.getId());
        System.out.println(task);

    }
}
