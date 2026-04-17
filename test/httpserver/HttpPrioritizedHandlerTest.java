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
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpPrioritizedHandlerTest extends HttpBaseHandlerTest<TaskManager> {

    private Task task1;
    private Task task2;
    private Task task3;


    // инициализируем три задачи с разным временем, через api, а не менеджер
    @BeforeEach
    void init() {
        // задача с самым ранним временем
        task1 = new Task("task early", "description",
                LocalDateTime.of(2020, 1, 1, 10, 0).toInstant(ZoneOffset.UTC),
                Duration.ofMinutes(10));
        // задача с поздним временем
        task2 = new Task("task late", "description",
                LocalDateTime.of(2020,1,1,10,20).toInstant(ZoneOffset.UTC),
                Duration.ofMinutes(10));
        // задача с отсутствием времени
        task3 = new Task("task no time", "description", null, null);

        try {
            sendPostRequest(URI_TASK, jsonMapper.toJson(task1));
            sendPostRequest(URI_TASK, jsonMapper.toJson(task2));
            sendPostRequest(URI_TASK, jsonMapper.toJson(task3));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected TaskManager initManager() {
        return Managers.getDefault();
    }

    @Test
    void getPriorityTest() throws IOException, InterruptedException {
        // получаем список через api
        HttpResponse<String> response = sendGetRequest(URI_PRIORITY);
        assertEquals(200, response.statusCode(), "Не удалось получить список приоритета");
        Type listType = TypeToken.getParameterized(List.class, Task.class).getType();
        List<Task> actualList = jsonMapper.fromJson(response.body(), listType);

        assertEquals(2, actualList.size(), "Количество задач не совпадает");
        assertEquals(task1.getName(), actualList.get(0).getName(), "Первая задача должна быть с самым ранним временем");
        assertEquals(task2.getName(), actualList.get(1).getName(),"Вторая задача должна быть с самым поздним временем");
    }

}
