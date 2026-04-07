package manager;

import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.Instant;
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

    @Test
    void addHistoryEpic() {
        int expectedValue = 1;
        epic = manager.createEpic(epic);
        manager.getEpic(epic.getId());
        List<Task> epics = manager.getHistory();
        int actualValue = epics.size();

        assertEquals(expectedValue, actualValue, "History should have been empty");
    }

    @Test
    void deleteHistoryEpic() {
        int expectedValue = 0;
        epic = manager.createEpic(epic);
        manager.getEpic(epic.getId());
        manager.deleteEpic(epic.getId());
        List<Task> epics = manager.getHistory();
        int actualValue = epics.size();

        assertEquals(expectedValue, actualValue, "History should have been empty");
    }

    @Test
    void deleteAllHistoryEpics() {
        int expectedValue = 0;
        epic = manager.createEpic(epic);
        manager.getEpic(epic.getId());
        manager.deleteAllEpics();
        List<Task> epics = manager.getHistory();
        int actualValue = epics.size();

        assertEquals(expectedValue, actualValue, "History should have been empty");
    }

    @Test
    void addHistorySubtask() {
        int expectedValue = 1;
        epic = manager.createEpic(epic);
        Subtask subtask = createSubtaskInEpic(epic);
        manager.getSubtask(subtask.getId());
        List<Task> subtasks = manager.getHistory();
        int actualValue = subtasks.size();

        assertEquals(expectedValue, actualValue, "History should have been empty");
    }

    @Test
    void deleteHistorySubtask() {
        int expectedValue = 0;
        epic = manager.createEpic(epic);
        Subtask subtask = createSubtaskInEpic(epic);
        manager.getSubtask(subtask.getId());
        manager.deleteSubtask(subtask.getId());
        List<Task> subtasks = manager.getHistory();
        int actualValue = subtasks.size();

        assertEquals(expectedValue, actualValue, "History should have been empty");
    }

    @Test
    void deleteAllHistorySubtasks() {
        int expectedValue = 0;
        epic = manager.createEpic(epic);
        Subtask subtask = createSubtaskInEpic(epic);
        manager.getSubtask(subtask.getId());
        manager.deleteAllSubtasks();
        List<Task> subtasks = manager.getHistory();
        int actualValue = subtasks.size();

        assertEquals(expectedValue, actualValue, "History should have been empty");
    }


    @Test
    void shouldBePreservingInsertionOrderInHistory() {
        String expectedName = "2";

        task = manager.createTask(task);
        Task task2 = manager.createTask(new Task("2", "description", Instant.now(), Duration.ofMinutes(10)));
        manager.getTask(task.getId());
        manager.getTask(task2.getId());
        List<Task> tasks = manager.getHistory();
        String actualName = tasks.get(1).getName();

        assertEquals(expectedName, actualName, "History should have been the same");
    }

    @Test
    void shouldBeSaveCurrentViewStateInHistory() {
        String expectedName = "2";

        task = manager.createTask(task);
        Task task2 = manager.createTask(new Task("2", "description", Instant.now(), Duration.ofMinutes(10)));
        manager.getTask(task.getId());
        manager.getTask(task2.getId());
        manager.getTask(task.getId());
        List<Task> tasks = manager.getHistory();
        String actualName = tasks.getFirst().getName();

        assertEquals(expectedName, actualName, "History should have been the same");
    }
}
