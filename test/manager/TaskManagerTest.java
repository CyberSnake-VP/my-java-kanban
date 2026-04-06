package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest <T extends TaskManager> {

    protected T manager;
    protected Task task;
    protected Epic epic;

    @BeforeEach
    abstract void setUp();

    public Subtask createSubtaskInEpic(Epic epicWithId) {
        return manager.createSubtask(new Subtask("name", "description", Status.NEW, Instant.now(), Duration.ofMinutes(1), epicWithId));
    }


    @Test
    void createTask() {
        final String expectedName = "name";
        final String expectedDescription = "description";
        final Status expectedStatus = Status.NEW;

        Task expectedTask = manager.createTask(task);
        Task actualTask = manager.getTask(expectedTask.getId());
        Task actualCopyTaskMustBeNull = manager.createTask(expectedTask);
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

    @Test
    void deleteAllTasks() {
        manager.createTask(task);
        manager.deleteAllTasks();
        ArrayList<Task> actualTasks = manager.getTasks();
        assertEquals(0, actualTasks.size(), "Task should not be empty");
    }


    @Test
    void createEpic() {
        final String expectedName = "name";
        final String expectedDescription = "description";
        final Status expectedStatus = Status.NEW;

        Epic expectedEpic = manager.createEpic(epic);
        Epic actualEpic = manager.getEpic(expectedEpic.getId());
        Epic actualEpicCopy = manager.createEpic(epic);
        final String actualName = actualEpic.getName();
        final String actualDescription = actualEpic.getDescription();
        final Status actualStatus = actualEpic.getStatus();
        assertNotNull(actualEpic, "Epic should not be null");
        assertNull(actualEpicCopy, "Epic copy should be null");
        assertEquals(expectedName, actualName, "Epic name should be the same");
        assertEquals(expectedDescription, actualDescription, "Epic description should be the same");
        assertEquals(expectedStatus, actualStatus, "Epic status should be the same");
    }

    @Test
    void updateEpic() {
        final String expectedName = "nameUpdate";
        final String expectedDescription = "descriptionUpdate";
        final Status expectedStatus = Status.IN_PROGRESS;

        Epic expectedEpic = manager.createEpic(epic);

        expectedEpic.setName(expectedName);
        expectedEpic.setDescription(expectedDescription);
        expectedEpic.setStatus(expectedStatus);
        Epic actualEpic = manager.updateEpic(expectedEpic);

        assertNotNull(actualEpic, "Epic should not be null");
        assertEquals(expectedName, actualEpic.getName(), "Epic name should be the same");
        assertEquals(expectedDescription, actualEpic.getDescription(), "Epic description should be the same");
        assertNotEquals(expectedStatus, actualEpic.getStatus(), "Epic status should not be the same");
    }

    @Test
    void deleteEpic() {
        Epic expectedEpic = manager.createEpic(epic);
        manager.deleteEpic(expectedEpic.getId());
        Epic actualEpic = manager.getEpic(expectedEpic.getId());
        assertNull(actualEpic, "Epic should be null");
    }

    @Test
    void getEpics() {
        final String expectedName = "name";
        final String expectedDescription = "description";
        manager.createEpic(epic);
        ArrayList<Epic> actualEpics = manager.getEpics();
        assertNotNull(actualEpics, "Epic should not be null");
        assertEquals(1, actualEpics.size(), "Epic should not be empty");
        assertEquals(expectedName, actualEpics.getFirst().getName(), "Epic name should be the same");
        assertEquals(expectedDescription, actualEpics.getFirst().getDescription(), "Epic description should be the same");
    }

    @Test
    void deleteEpics() {
        manager.createEpic(epic);
        manager.createEpic(new Epic("name", "description"));
        manager.deleteAllEpics();
        ArrayList<Epic> actualEpics = manager.getEpics();
        assertEquals(0, actualEpics.size(), "Epic should be empty");
    }




    @Test
    void createSubtask() {
        final String expectedName = "name";
        final String expectedDescription = "description";
        final Status expectedStatus = Status.NEW;

        Epic expectedEpic = manager.createEpic(epic);
        Subtask expectedSubtask = createSubtaskInEpic(expectedEpic);
        Subtask actualSubtask = manager.getSubtask(expectedSubtask.getId());
        final String actualName = actualSubtask.getName();
        final String actualDescription = actualSubtask.getDescription();
        final Status actualStatus = actualSubtask.getStatus();

        assertNotNull(actualSubtask, "Subtask should not be null");
        assertEquals(expectedName, actualName, "Subtask name should be the same");
        assertEquals(expectedDescription, actualDescription, "Subtask description should be the same");
        assertEquals(expectedStatus, actualStatus, "Subtask status should be the same");
    }

    @Test
    void updateSubtask() {
        final String expectedName = "nameUpdate";
        final String expectedDescription = "descriptionUpdate";
        final Status expectedStatus = Status.IN_PROGRESS;

        Epic expectedEpic = manager.createEpic(epic);
        Subtask expectedSubtask = createSubtaskInEpic(expectedEpic);
        expectedSubtask.setName(expectedName);
        expectedSubtask.setDescription(expectedDescription);
        expectedSubtask.setStatus(expectedStatus);
        Subtask actualSubtask = manager.updateSubtask(expectedSubtask);
        final String actualName = actualSubtask.getName();
        final String actualDescription = actualSubtask.getDescription();
        final Status actualStatus = actualSubtask.getStatus();


        assertNotNull(actualSubtask, "Subtask should not be null");
        assertEquals(expectedName, actualName, "Subtask name should be the same");
        assertEquals(expectedDescription, actualDescription, "Subtask description should be the same");
        assertEquals(expectedStatus, actualStatus, "Subtask status should be the same");
    }

    @Test
    void deleteSubtask() {
        Epic expectedEpic = manager.createEpic(epic);
        Subtask expectedSubtask = createSubtaskInEpic(expectedEpic);

        manager.deleteSubtask(expectedSubtask.getId());
        Subtask actualSubtask = manager.getSubtask(expectedSubtask.getId());

        assertNull(actualSubtask, "Subtask should be null");
    }

    @Test
    void getSubtasks() {
        final String expectedName = "name";
        final String expectedDescription = "description";
        final Status expectedStatus = Status.NEW;
        Epic expectedEpic = manager.createEpic(epic);
        createSubtaskInEpic(expectedEpic);

        ArrayList<Subtask> actualSubtasks = manager.getSubtasks();

        assertNotNull(actualSubtasks, "Subtasks should not be null");
        assertEquals(1, actualSubtasks.size(), "Subtasks should not be empty");
        assertEquals(expectedName, actualSubtasks.getFirst().getName(), "Subtask name should be the same");
        assertEquals(expectedDescription, actualSubtasks.getFirst().getDescription(), "Subtask description should be the same");
        assertEquals(expectedStatus, actualSubtasks.getFirst().getStatus(), "Subtask status should be the same");
    }

    @Test
    void deleteAllSubtasks() {
        Epic expectedEpic = manager.createEpic(epic);
        createSubtaskInEpic(expectedEpic);

        manager.deleteAllSubtasks();
        ArrayList<Subtask> actualSubtasks = manager.getSubtasks();

        assertNotNull(actualSubtasks, "Subtasks should not be null");
        assertEquals(0, actualSubtasks.size(), "Subtasks should not be empty");
    }

    @Test
    void shouldBeEpicStatusNew() {
        Status expectedStatus = Status.NEW;

        Epic expectedEpic = manager.createEpic(epic);
        Subtask expectedSubtask = createSubtaskInEpic(expectedEpic);
        expectedSubtask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(expectedSubtask);
        manager.deleteSubtask(expectedSubtask.getId());
        Epic actualEpic = manager.getEpic(expectedEpic.getId());
        final Status actualStatus = actualEpic.getStatus();

        assertEquals(expectedStatus, actualStatus, "Epic status should be the same");
    }

    @Test
    void shouldBeEpicStatusInProgress() {
        Status expectedStatus = Status.IN_PROGRESS;

         Epic expectedEpic = manager.createEpic(epic);
         Subtask expectedSubtask = createSubtaskInEpic(expectedEpic);
         Subtask expectedSubtask2 = manager.createSubtask(new Subtask("name", "description", Status.NEW, Instant.now(), Duration.ofMinutes(1), epic));
         expectedSubtask.setStatus(Status.NEW);
         expectedSubtask2.setStatus(Status.DONE);
         manager.updateSubtask(expectedSubtask);
         manager.updateSubtask(expectedSubtask2);
         Epic actualEpic = manager.getEpic(expectedEpic.getId());
         final Status actualStatus = actualEpic.getStatus();

         assertEquals(expectedStatus, actualStatus, "Epic status should be the same");
    }
}
