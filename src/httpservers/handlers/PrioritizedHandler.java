package httpservers.handlers;

import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class PrioritizedHandler extends BaseHandler {
    private static final int PATH_SIZE = 2;

    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }


    @Override
    void processGet(String path, HttpExchange exchange) {
        try {
            String[] elements  = path.split("/");
            if(elements.length == PATH_SIZE) {
                getPrioritizedList(exchange);
                return;
            }
            sendResponse(exchange, NOT_FOUND, NOT_FOUND_MESSAGE);

        } catch (IOException e) {
            sendErrorResponse(exchange, INTERNAL_SERVER_ERROR, e.getMessage());
        }


    }

    private void getPrioritizedList(HttpExchange exchange) throws IOException {
        Type listTaskType = TypeToken.getParameterized(List.class, Task.class).getType();
        List<Task> prioritizedTask = manager.getPrioritized();
        if(prioritizedTask.isEmpty()) {
            sendResponse(exchange, OK, "[]");
            return;
        }
        String json =  jsonMapper.toJson(prioritizedTask, listTaskType);
        sendResponse(exchange, OK, json);
    }

    @Override
    void processPost(String path, HttpExchange exchange) {

    }

    @Override
    void processDelete(String path, HttpExchange exchange) {

    }
}
