package manager;

import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryHistoryManagerTest extends InMemoryTaskManagerTest{

    @Test
    void addHistoryTask() {
        int expectedValue = 1;
        String expectedName = "name";
        String expectedDescription = "description";
        Status expectedStatus = Status.NEW;
        task = manager.createTask(task);
        manager.getTask(task.getId());

        List<Task> tasks = manager.getHistory();
        int actualValue = tasks.size();
        String actualName = tasks.getFirst().getName();
        String actualDescription = tasks.getFirst().getDescription();
        Status actualStatus = tasks.getFirst().getStatus();

        assertNotNull(tasks, "History should not be null");
        assertEquals(expectedValue, actualValue, "History should have been created");
        assertEquals(expectedName, actualName, "History should have been created");
        assertEquals(expectedDescription, actualDescription, "History should have been created");
        assertEquals(expectedStatus, actualStatus, "History should have been created");
    }

    @Test
    void deleteHistoryTask() {
        int expectedValue = 0;
        task = manager.createTask(task);
        manager.getTask(task.getId());
        manager.deleteTask(task.getId());
        List<Task> tasks = manager.getHistory();
        int actualValue = tasks.size();


        assertEquals(expectedValue, actualValue, "History should have been empty");
    }

    @Test
    void deleteAllHistoryTasks() {
        int expectedValue = 0;
        task = manager.createTask(task);
        manager.getTask(task.getId());
        manager.deleteAllTasks();
        List<Task> tasks = manager.getHistory();
        int actualValue = tasks.size();

        assertEquals(expectedValue, actualValue, "History should have been empty");
    }
}
