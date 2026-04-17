package httpserver;

import com.google.gson.reflect.TypeToken;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    @Test
    void deleteSubtaskTest() throws IOException, InterruptedException {
        // создаем
        String json = jsonMapper.toJson(subtask, Subtask.class);
        HttpResponse<String> response = sendPostRequest(URI_SUBTASK, json);
        assertEquals(201, response.statusCode(), "Статусы не совпадают");

        // получаем
        HttpResponse<String> delResponse = sendDeleteRequest(URI_SUBTASK_BY_ID);
        assertEquals(204, delResponse.statusCode(), "Кода не совпадает");
        Subtask sub = jsonMapper.fromJson(response.body(), Subtask.class);
        int id = sub.getId();

        assertNull(manager.getSubtask(id), "Подзадача должна быть удалена");
    }

    @Test
    void getSubtasksTest() throws IOException, InterruptedException {
        // создаем
        String json = jsonMapper.toJson(subtask, Subtask.class);
        HttpResponse<String> response = sendPostRequest(URI_SUBTASK, json);
        Subtask created = jsonMapper.fromJson(response.body(), Subtask.class);
        assertEquals(201, response.statusCode(), "Не удалось создать подзадачу");

        // получаем
        HttpResponse<String> getResponse = sendGetRequest(URI_SUBTASK_BY_ID);
        Subtask actual = jsonMapper.fromJson(getResponse.body(), Subtask.class);
        assertEquals(200, getResponse.statusCode(), "Не удалось получить задачу");


        assertEquals(created.getId(), actual.getId());
        assertEquals(subtask.getName(), actual.getName());
        assertEquals(subtask.getDescription(), actual.getDescription());
        assertEquals(subtask.getEpicId(), actual.getEpicId());
        assertEquals(Status.NEW, actual.getStatus());
    }
    @Test
    void getAllSubtasksTest() throws IOException, InterruptedException {
        // создаем
        String json = jsonMapper.toJson(subtask, Subtask.class);
        HttpResponse<String> response = sendPostRequest(URI_SUBTASK, json);
        assertEquals(201,  response.statusCode(), "Не удалось создать подзадачу");
        Task createdTask = jsonMapper.fromJson(response.body(), Task.class);

        // получаем
        Type subtaskTypeList = new TypeToken<List<Subtask>>(){}.getType();
        HttpResponse<String> getResponse = sendGetRequest(URI_SUBTASK);
        assertEquals(200, getResponse.statusCode(), "Не удалось получить подзадачу");
        List<Subtask> actualList = jsonMapper.fromJson(getResponse.body(), subtaskTypeList);
        Subtask actual = actualList.getFirst();
        List<Subtask> actualSubtaskInManager = manager.getSubtasks();
        Subtask actualSubInManager = actualSubtaskInManager.getFirst();


        assertEquals(createdTask.getId(), actual.getId());
        assertEquals(createdTask.getName(), actual.getName());
        assertEquals(createdTask.getDescription(), actual.getDescription());
        assertEquals(createdTask.getName(), actualSubInManager.getName());
        assertEquals(createdTask.getDescription(), actualSubInManager.getDescription());
        assertEquals(createdTask.getStatus(), actualSubInManager.getStatus());
    }

    @Test
    void updateSubtaskTest() throws IOException, InterruptedException {
        // создаем
        String json = jsonMapper.toJson(subtask, Subtask.class);
        HttpResponse<String> postResponse = sendPostRequest(URI_SUBTASK, json);
        assertEquals(201, postResponse.statusCode(), "не удалось создать задачу");
        Subtask createdSub = jsonMapper.fromJson(postResponse.body(), Subtask.class);
        createdSub.setName("updated");
        createdSub.setDescription("updated");
        createdSub.setStatus(Status.IN_PROGRESS);

        // обновляем
        String updatedJson = jsonMapper.toJson(createdSub, Subtask.class);
        HttpResponse<String> updateResponse = sendPostRequest(URI_SUBTASK, updatedJson);
        assertEquals(200,  updateResponse.statusCode(), "Не удалось обновить задачу");
        Subtask updated = jsonMapper.fromJson(updateResponse.body(), Subtask.class);

        // получаем
        HttpResponse<String> getResponse = sendGetRequest(URI_SUBTASK_BY_ID);
        assertEquals(200, getResponse.statusCode(), "Не удалось получить задачу");
        Subtask actual = jsonMapper.fromJson(getResponse.body(), Subtask.class);

        assertEquals(updated.getName(), actual.getName(), "Имена не совпадают");
        assertEquals(updated.getDescription(), actual.getDescription(), "Описание не совпадает");
        assertEquals(updated.getStatus(), actual.getStatus(), "Статус не совпадает");
        assertEquals(updated.getEpicId(), actual.getEpicId(), "EpicId должны совпадать");
        assertEquals(updated.getStartTime(), actual.getStartTime(), "Время начала не совпадает");
        assertEquals(updated.getDuration(), actual.getDuration(), "Продолжительность не совпадает");
    }

}
