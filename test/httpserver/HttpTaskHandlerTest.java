package httpserver;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskHandlerTest extends HttpBaseHandlerTest<TaskManager> {

    Task task = new Task("task", "description", Instant.now(), Duration.ofMinutes(10));

    @Override
    protected TaskManager initManager() {
        return Managers.getDefault();
    }


    @Test
    void addTaskTest() throws IOException, InterruptedException {
        // конвертируем задачу в json
        String json = jsonMapper.toJson(task);

        // http-запрос
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI_TASK)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        // обработчик ответа (чтобы перевести тело в String) HttpResponse.BodyHandlers.ofString()
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(201, response.statusCode(), "Код ответа не совпадает");

        // проверяем задачу в manager после создания
        List<Task> taskFromManager = manager.getTasks();

        assertEquals(1, taskFromManager.size(), "Количество задач не совпадает");
        assertEquals("task", taskFromManager.getFirst().getName());
    }

}
