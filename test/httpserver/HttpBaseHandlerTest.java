package httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import httpservers.HttpTaskServer;
import httpservers.adapters.DurationAdapter;
import httpservers.adapters.InstantAdapter;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import tasks.Epic;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.Instant;

public abstract class HttpBaseHandlerTest <T extends TaskManager>{
    // базовый тестовый класс
    // manager
    protected T manager;
    // HttpTaskServer наш класс сервер
    protected HttpTaskServer server;

    protected final Gson jsonMapper = new GsonBuilder()

            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    // http-client
    HttpClient httpClient = HttpClient.newHttpClient();

    // URI Запросов для тестов
    protected final URI URI_TASK = URI.create("http://localhost:8080/tasks");
    protected final URI URI_TASK_BY_ID = URI.create("http://localhost:8080/tasks/1");
    protected final URI URI_EPIC = URI.create("http://localhost:8080/epics");
    protected final URI URI_EPIC_BY_ID = URI.create("http://localhost:8080/epics/1");
    protected final URI URI_SUBTASK = URI.create("http://localhost:8080/subtasks");
    protected final URI URI_SUBTASK_BY_ID = URI.create("http://localhost:8080/subtasks/1");


    // перезапишем метод setUp
    @BeforeEach
    void setUp() throws IOException {
        manager = initManager();
        server = new HttpTaskServer(manager);
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
        server.start();
    }

    protected abstract T initManager();

    // после каждого метода останавливаем сервер, чтобы освободить порт, потому в setUp() стартует снова.
    @AfterEach
    void tearDown() {
        server.stop();
    }

}
