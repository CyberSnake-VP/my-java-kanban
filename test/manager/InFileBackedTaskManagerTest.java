package manager;

import exceptions.ManagerBackupException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

public class InFileBackedTaskManagerTest extends InMemoryTaskManagerTest {
    private FileBackedTaskManager fm;
    private File tempFile;

    @BeforeEach
    void initFileBackedTaskManager() {
        try {
            tempFile = File.createTempFile("test", ".csv");
            fm = new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            System.out.println("Ошибка при создании FileBackedTaskManager");
        }
    }


    @Test
    void shouldBeSaveAndLoadFromFile() {
        Task expectedTask = fm.createTask(task);
        Epic expectedEpic = fm.createEpic(epic);
        Subtask expectedSubtask = fm.createSubtask(
                new Subtask("name", "description", LocalDateTime.now().toInstant(ZoneOffset.UTC), Duration.ofMinutes(1), expectedEpic)
        );
        Task expectedNewTask = new Task("newTask", "description", LocalDateTime.now().toInstant(ZoneOffset.UTC),Duration.ofMinutes(1));

        fm = FileBackedTaskManager.loadFromFile(fm.getData());
        Task actualTask = fm.getTask(expectedTask.getId());
        Epic actualEpic = fm.getEpic(expectedEpic.getId());
        Subtask actualSubtask = fm.getSubtask(expectedSubtask.getId());
        Task actualNewTask = fm.createTask(expectedNewTask);

        assertTrue(tempFile.exists(), "File should exist");
        assertTrue(tempFile.length() > 0, "File length should be greater than 0");

        assertNotNull(actualTask, "Task should not be null");
        assertNotNull(actualEpic, "Epic should not be null");
        assertNotNull(actualSubtask, "Subtask should not be null");
        assertNotNull(actualNewTask, "New Task should not be null");
        assertEquals(expectedTask, actualTask, "Task should be the same");
        assertEquals(expectedEpic, actualEpic, "Epic should be the same");
        assertEquals(expectedSubtask, actualSubtask, "Subtask should be the same");
        assertEquals(expectedNewTask.getName(), actualNewTask.getName(), "New Task should be the same");
    }

    @Test
    void shouldBeExceptionManagerBackupException() {
        assertThrows(ManagerBackupException.class, () -> {
            fm = FileBackedTaskManager.loadFromFile(new File("test"));
        }, "Должно быть выброшено исключение ManagerBackupException");
    }
}
