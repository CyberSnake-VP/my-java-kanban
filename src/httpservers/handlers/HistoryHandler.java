package httpservers.handlers;

import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class HistoryHandler extends BaseHandler{
   private final static int PATH_WITHOUT_IDX = 2;

    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    void processGet(String path, HttpExchange exchange) {
        try {
            String[] elements = path.split("/");
            if(elements.length == PATH_WITHOUT_IDX){
                getAllHistory(exchange);
                return;
            }
            sendResponse(exchange, NOT_FOUND, NOT_FOUND_MESSAGE);
        } catch (IOException e) {
            sendErrorResponse(exchange, INTERNAL_SERVER_ERROR, e.getMessage());
        }


    }

    private void getAllHistory(HttpExchange exchange) throws IOException {
        List<Task> tasks = manager.getHistory();
        if(tasks.isEmpty()){
            sendResponse(exchange, OK, "[]");
            return;
        }
        Type listTaskType = TypeToken.getParameterized(List.class, Task.class).getType();
        String json = jsonMapper.toJson(tasks,  listTaskType);
        sendResponse(exchange, OK, json);
    }

    @Override
    void processPost(String path, HttpExchange exchange) {

    }

    @Override
    void processDelete(String path, HttpExchange exchange) {

    }
}
