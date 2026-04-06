package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest <T extends TaskManager> {

    protected T manager;
    protected Task task;
    protected Epic epic;

    @BeforeEach
    abstract void setUp();


    @Test
    void createTask() {
        final String expectedName = "name";
        final String expectedDescription = "description";
        final Status expectedStatus = Status.NEW;

        Task expectedTask = manager.createTask(task);
        Task actualTask = manager.getTask(expectedTask.getId());
        Task actualCopyTaskMustBeNull = manager.createTask(task);
        final String actualName = actualTask.getName();
        final String actualDescription = actualTask.getDescription();
        final Status actualStatus = actualTask.getStatus();

        assertNotNull(actualTask, "Task should not be null");
        assertNull(actualCopyTaskMustBeNull, "Copy task should not be null");
        assertEquals(expectedName, actualName, "Task name should be the same");
        assertEquals(expectedDescription, actualDescription, "Task description should be the same");
        assertEquals(expectedStatus, actualStatus, "Task status should be the same");
    }

    @Test
    void updateTask() {
         final String expectedName = "nameUpdate";
         final String expectedDescription = "descriptionUpdate";
         final Status expectedStatus = Status.IN_PROGRESS;

         Task expectedTask = manager.createTask(task);
         Task actualTask = manager.getTask(expectedTask.getId());

         actualTask.setName(expectedName);
         actualTask.setDescription(expectedDescription);
         actualTask.setStatus(expectedStatus);
         manager.updateTask(actualTask);

         actualTask = manager.getTask(expectedTask.getId());

         assertNotNull(actualTask, "Task should not be null");
         assertEquals(expectedName, actualTask.getName(), "Task name should be the same");
         assertEquals(expectedDescription, actualTask.getDescription(), "Task description should be the same");
         assertEquals(expectedStatus, actualTask.getStatus(), "Task status should be the same");

    }

    @Test
    void deleteTask() {

        Task createdTask = manager.createTask(task);
        manager.deleteTask(createdTask.getId());
        Task actualTask = manager.getTask(createdTask.getId());

        assertNull(actualTask, "Task should be null");
    }

    @Test
    void getTasks() {
        final String expectedName = "name";
        final String expectedDescription = "description";
        final Status expectedStatus = Status.NEW;

        manager.createTask(task);
        ArrayList<Task> actualTasks = manager.getTasks();

        assertNotNull(actualTasks, "Task should not be null");
        assertEquals(expectedName, actualTasks.getFirst().getName(), "Task name should be the same");
        assertEquals(expectedDescription, actualTasks.getFirst().getDescription(), "Task description should be the same");
        assertEquals(expectedStatus, actualTasks.getFirst().getStatus(), "Task status should be the same");
    }
}
