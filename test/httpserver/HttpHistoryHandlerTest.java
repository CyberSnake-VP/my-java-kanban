package httpserver;

import com.google.gson.reflect.TypeToken;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpHistoryHandlerTest extends HttpBaseHandlerTest<TaskManager> {
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void init() throws IOException {
        task = new Task("task", "description", LocalDateTime.of(2026,1,1, 1, 0, 0).toInstant(ZoneOffset.UTC), Duration.ofMinutes(10));
        epic = new Epic("epic", "description");
        epic = manager.createEpic(epic);
        subtask = new Subtask("subtask", "description", LocalDateTime.of(2026,1,1, 1, 10, 0).toInstant(ZoneOffset.UTC), Duration.ofMinutes(10), epic);
    }

    @Override
    protected TaskManager initManager() {
        return Managers.getDefault();
    }

    @Test
    void getTaskHistoryTest() throws IOException, InterruptedException {
        int expectedSize = 3;

        // создаем
        String jsonTask = jsonMapper.toJson(task);
        String jsonSubtask = jsonMapper.toJson(subtask);
        HttpResponse<String> postResp = sendPostRequest(URI_TASK,  jsonTask);
        HttpResponse<String> postResp3 = sendPostRequest(URI_SUBTASK,  jsonSubtask);
        assertEquals(201, postResp.statusCode(), "Не удалось создать задачу");
        assertEquals( 201, postResp3.statusCode(), "Не удалось создать подзадачу");
        Task createdTask = jsonMapper.fromJson(postResp.body(), Task.class);
        Subtask createdSubtask = jsonMapper.fromJson(postResp3.body(), Subtask.class);

        // записываем в историю
        HttpResponse<String> getResp = sendGetRequest(URI.create("http://localhost:8080/tasks/" + createdTask.getId()));
        HttpResponse<String> getResp2 = sendGetRequest(URI.create("http://localhost:8080/epics/" + epic.getId()));
        HttpResponse<String> getResp3 = sendGetRequest(URI.create("http://localhost:8080/subtasks/" + createdSubtask.getId()));
        assertEquals(200, getResp.statusCode(), "Не удалось получить задачу");
        assertEquals(200, getResp2.statusCode(), "Не удалось получить эпик");
        assertEquals(200, getResp3.statusCode(), "Не удалось получить подзадачу");
        Task actualTask = jsonMapper.fromJson(getResp.body(), Task.class);
        Epic actualEpic = jsonMapper.fromJson(getResp2.body(), Epic.class);
        Subtask actualSubtask = jsonMapper.fromJson(getResp3.body(), Subtask.class);


        // получаем список истории
        HttpResponse<String> getHistoryResp = sendGetRequest(URI_HISTORY);
        assertEquals(200, getHistoryResp.statusCode(), "Не удалось получить список истории");
        Type historyListType = TypeToken.getParameterized(List.class, Task.class).getType();
        List<Task> actualHistory = jsonMapper.fromJson(getHistoryResp.body(), historyListType);

        assertEquals(expectedSize, actualHistory.size(), "Количество задач разное");
        assertEquals(actualTask.getName(), actualHistory.get(0).getName(), "Задачи не совпадают");
        assertEquals(actualEpic.getName(), actualHistory.get(1).getName(), "Эпики не совпадают");
        assertEquals(actualSubtask.getName(), actualHistory.get(2).getName(), "Подзадачи не совпадают");
    }

}
