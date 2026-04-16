package httpserver;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpEpicHandlerTest extends HttpBaseHandlerTest<TaskManager> {

    private Epic epic;

    @BeforeEach
    void createTestData() {
        epic = new Epic("epic", "description");
    }

    @Override
    protected TaskManager initManager() {
        return Managers.getDefault();
    }

    @Test
    void createEpicTest() throws IOException, InterruptedException {
        String json = jsonMapper.toJson(epic);
        HttpResponse<String> response = sendPostRequest(URI_EPIC, json);
        assertEquals(201, response.statusCode(), "Не совпадает код ответа");
        List<Epic> epics = manager.getEpics();
        assertEquals(1, epics.size(), "Должен быть один эпик");
        assertEquals("epic", epics.getFirst().getName(), "название не соответствуют");
    }


}


