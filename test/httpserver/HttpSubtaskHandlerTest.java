package httpserver;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpSubtaskHandlerTest extends HttpBaseHandlerTest<TaskManager> {

    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void createTestData() {
        epic = new Epic("epic", "description");
        Epic EpicWithId = manager.createEpic(epic);
        subtask = new Subtask("subtask", "description", Instant.now(), Duration.ofMinutes(10), EpicWithId);
    }

    @Override
    protected TaskManager initManager() {
        return Managers.getDefault();
    }


    @Test
    void createSubtaskTest() throws IOException, InterruptedException {
        String json = jsonMapper.toJson(subtask, Subtask.class);
        HttpResponse<String> response = sendPostRequest(URI_SUBTASK, json);
        assertEquals(201, response.statusCode(), "неверный код ответа");
        List<Subtask> subtasks = manager.getSubtasks();
        assertEquals(1, subtasks.size(), "должна быть одна подзадача");
        assertEquals("subtask", subtasks.getFirst().getName(), "название задачи не совпадают");
        assertEquals("description", subtasks.getFirst().getDescription());
        assertEquals(Status.NEW, subtasks.getFirst().getStatus(), "статус должен быть NEW");
    }


}
