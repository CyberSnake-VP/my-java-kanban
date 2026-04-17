package httpserver;

import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonToken;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskHandlerTest extends HttpBaseHandlerTest<TaskManager> {

    Task task;

    @BeforeEach
    void createTestData() {
        task = new Task("task", "description", Instant.now(), Duration.ofMinutes(10));
    }

    @Override
    protected TaskManager initManager() {
        return Managers.getDefault();
    }

    @Test
    void addTaskTest() throws IOException, InterruptedException {
        // конвертируем задачу в json
        String json = jsonMapper.toJson(task);
        // отправляем запрос на сервер
        HttpResponse<String> response = sendPostRequest(URI_TASK, json);
        // проверяем код ответа
        assertEquals(201, response.statusCode(), "Код ответа не совпадает");
        // проверяем задачу в manager после создания
        List<Task> taskFromManager = manager.getTasks();

        assertEquals(1, taskFromManager.size(), "Количество задач не совпадает");
        assertEquals("task", taskFromManager.getFirst().getName());
    }

    @Test
    void updateTaskTest() throws IOException, InterruptedException {
        String expectedName = "update";
        String expectedDescription = "update";
        Status exceptedStatus = Status.IN_PROGRESS;

        String json = jsonMapper.toJson(task);
        HttpResponse<String> respPost = sendPostRequest(URI_TASK, json);
        assertEquals(201, respPost.statusCode(), "код ответа не верный");

        Task taskForUpdate = jsonMapper.fromJson(respPost.body(), Task.class);
        taskForUpdate.setName(expectedName);
        taskForUpdate.setDescription(expectedDescription);
        taskForUpdate.setStatus(exceptedStatus);
        String updatedJson = jsonMapper.toJson(taskForUpdate);
        HttpResponse<String> resp = sendPostRequest(URI_TASK, updatedJson);
        assertEquals(200, resp.statusCode(), "Код ответа не совпадает");

        List<Task> tasks = manager.getTasks();
        Task updatedTask = tasks.getFirst();
        String actualName = updatedTask.getName();
        String actualDescription = updatedTask.getDescription();
        Status actualStatus = updatedTask.getStatus();

        assertEquals(expectedName, actualName, "Название задачи не совпадает");
        assertEquals(expectedDescription, actualDescription, "Описание задачи не совпадает");
        assertEquals(exceptedStatus, actualStatus, "Статус не совпадает");
    }

    @Test
    void deleteTaskTest() throws IOException, InterruptedException {
        int expectedCount = 0;
        String json = jsonMapper.toJson(task);
        sendPostRequest(URI_TASK, json);
        HttpResponse<String> response = sendDeleteRequest(URI_TASK_BY_ID);
        assertEquals(204, response.statusCode(), "Код ответа не совпадает");
        List<Task> tasks = manager.getTasks();
        int actualCount = tasks.size();
        assertEquals(expectedCount, actualCount, "Задача не была удалена");
    }

    @Test
    void getAllTasksTest() throws IOException, InterruptedException {
        String expectedName = "task";
        String expectedDescription = "description";
        Status expectedStatus = Status.NEW;

        String json = jsonMapper.toJson(task);
        sendPostRequest(URI_TASK, json);
        HttpResponse<String> response = sendGetRequest(URI_TASK);
        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        List<Task> tasks = manager.getTasks();
        // для работы со списками и дженериками, нужно использовать указания типа, для правильно десериализации, через TypeToken<List<Task>>
        Type taskListType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> actualTasks = jsonMapper.fromJson(response.body(), taskListType);
        assertEquals(tasks.size(), actualTasks.size(), "количество задач не совпадает");
        String actualName = actualTasks.getFirst().getName();
        String actualDescription = actualTasks.getFirst().getDescription();
        Status actualStatus = actualTasks.getFirst().getStatus();

        assertEquals(expectedName, actualName, "название задачи не совпадает");
        assertEquals(expectedDescription, actualDescription, "описание задачи отличается");
        assertEquals(expectedStatus, actualStatus, "Статус задачи отличается");
    }

    @Test
    void getTaskByIdTest() throws IOException, InterruptedException {
        String expectedName = "task";
        String expectedDescription = "description";
        Status expectedStatus = Status.NEW;

        String json = jsonMapper.toJson(task);
        sendPostRequest(URI_TASK, json);
        HttpResponse<String> response = sendGetRequest(URI_TASK_BY_ID);
        Task getTask =  jsonMapper.fromJson(response.body(), Task.class);
        String actualName = getTask.getName();
        String actualDescription = getTask.getDescription();
        Status actualStatus = getTask.getStatus();

        assertEquals(expectedName, actualName, "название не совпадает");
        assertEquals(expectedDescription, actualDescription, "описание не совпадает");
        assertEquals(expectedStatus, actualStatus, "статус не совпадает");
    }

}
