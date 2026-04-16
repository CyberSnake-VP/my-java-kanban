package httpserver;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpEpicHandlerTest extends HttpBaseHandlerTest<TaskManager> {

    Epic epic = new Epic("epic", "description");

    @Override
    protected TaskManager initManager() {
        return Managers.getDefault();
    }

    @Test
    void createEpicTest() throws IOException, InterruptedException {
        String json = jsonMapper.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI_EPIC)
                .header("Content-Type", "application/json" )
                .timeout(Duration.ofSeconds(3))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Не совпадает код ответа");

    }


}


