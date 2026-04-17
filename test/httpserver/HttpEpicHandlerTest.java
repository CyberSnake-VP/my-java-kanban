package httpserver;

import com.google.gson.reflect.TypeToken;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Epic;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    @Test
    void getEpicTest() throws IOException, InterruptedException {
        String expectedName = "epic";
        String expectedDescription = "description";
        Status expectedStatus = Status.NEW;

        String json = jsonMapper.toJson(epic);
        sendPostRequest(URI_EPIC, json);
        HttpResponse<String> res = sendGetRequest(URI_EPIC_BY_ID);
        Epic actualEpic = jsonMapper.fromJson(res.body(), Epic.class);
        String actualName = actualEpic.getName();
        String actualDescription = actualEpic.getDescription();
        Status actualStatus = actualEpic.getStatus();

        assertEquals(expectedName, actualName, "Имена отличаются");
        assertEquals(expectedDescription, actualDescription, "Описание отличается");
        assertEquals(expectedStatus, actualStatus, "Статус задач отличается");
    }

    @Test
    void updateEpicTest() throws IOException, InterruptedException {
        String expectedName = "update";
        String expectedDescription = "update";

        String json = jsonMapper.toJson(epic);
        HttpResponse<String> resp = sendPostRequest(URI_EPIC, json);
        assertEquals(201, resp.statusCode(), "Код ответа не совпадает");
        HttpResponse<String> res = sendGetRequest(URI_EPIC_BY_ID);
        Epic actualEpic = jsonMapper.fromJson(res.body(), Epic.class);
        actualEpic.setName(expectedName);
        actualEpic.setDescription(expectedDescription);
        String updateJson = jsonMapper.toJson(actualEpic);
        HttpResponse<String> res2 = sendPostRequest(URI_EPIC, updateJson);
        assertEquals(200,  res2.statusCode(), "Кот ответа отличается");

        actualEpic =  jsonMapper.fromJson(res2.body(), Epic.class);
        String actualName = actualEpic.getName();
        String actualDescription = actualEpic.getDescription();

        assertEquals(expectedName, actualName, "Название отличается");
        assertEquals(expectedDescription, actualDescription, "Описание отличается");

        Epic epic = manager.getEpic(actualEpic.getId());
        assertEquals(expectedName, epic.getName());
        assertEquals(expectedDescription, epic.getDescription());

    }

    @Test
    void deleteEpicTest() throws IOException, InterruptedException {
        String json = jsonMapper.toJson(epic);
        HttpResponse<String> resp = sendPostRequest(URI_EPIC, json);
        assertEquals(201, resp.statusCode(), "Статус не совпадает");
        HttpResponse<String> delResp = sendDeleteRequest(URI_EPIC_BY_ID);
        assertEquals(204, delResp.statusCode(), "Статус не совпадает");

        Epic epic = jsonMapper.fromJson(resp.body(), Epic.class);
        int id =  epic.getId();
        assertNull(manager.getEpic(id), "Эпик должен быть удален");
    }

    @Test
    void getAllEpicsTest() throws IOException, InterruptedException {
        String expectedName = "epic";
        String expectedDescription = "description";
        Status expectedStatus = Status.NEW;

        String json = jsonMapper.toJson(epic);
        sendPostRequest(URI_EPIC, json);
        HttpResponse<String> resp = sendGetRequest(URI_EPIC);
        Type epicsList = new TypeToken<List<Epic>>(){}.getType();
        List<Epic> actualEpics = jsonMapper.fromJson(resp.body(), epicsList);
        String actualName = actualEpics.getFirst().getName();
        String actualDescription = actualEpics.getFirst().getDescription();
        Status actualStatus = actualEpics.getFirst().getStatus();
        List<Epic> epics = manager.getEpics();
        String actualNameInManager =  epics.getFirst().getName();
        String actualDescriptionInManager =  epics.getFirst().getDescription();

        assertEquals(expectedName, actualName, "Название отличаются");
        assertEquals(expectedDescription, actualDescription, "Описание отличается");
        assertEquals(expectedStatus, actualStatus, "Статус отличается");
        assertEquals(expectedName, actualNameInManager, "Названия отличаются");
        assertEquals(expectedDescription, actualDescriptionInManager, "Описание отличается");
    }


}


