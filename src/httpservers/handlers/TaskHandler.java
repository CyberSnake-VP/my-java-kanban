package httpservers.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.IntersectionsException;
import exceptions.JsonErrorConverterException;
import manager.TaskManager;
import status.Status;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class TaskHandler extends BaseHandler {

    private static final int PATH_WITHOUT_ID = 2;
    private static final int PATH_WITH_ID = 3;
    private static final int ID_IN_PATH = 2;

    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    void processGet(String path, HttpExchange exchange) {
        // в блоке try я буду ловить ошибки IOException от sendResponse()
        try {
            String[] elements = path.split("/");
            if (elements.length == PATH_WITHOUT_ID) {
                getAllTasks(exchange);
                return;

            } else if (elements.length == PATH_WITH_ID) {
                if (isNumber(elements[ID_IN_PATH])) {
                    int id = Integer.parseInt(elements[ID_IN_PATH]);
                    getOneTask(exchange, id);
                    return;
                }

                sendResponse(exchange, BAD_REQUEST, BAD_REQUEST_MESSAGE);
                return;
            }
            sendResponse(exchange, NOT_FOUND, NOT_FOUND_MESSAGE);
        } catch (IOException e) {
            System.out.println(ANSWER_SERVER_EXCEPTION + e.getMessage());
        }

    }

    private void getOneTask(HttpExchange exchange, int id) throws IOException {
        Task task = manager.getTask(id);
        if (task == null) {
            sendResponse(exchange, NOT_FOUND, NOT_FOUND_MESSAGE);
            return;
        }
        String jsonTask = jsonMapper.toJson(task, Task.class);
        sendResponse(exchange, OK, jsonTask);
    }

    private void getAllTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = manager.getTasks();
        String jsonTasks = jsonMapper.toJson(tasks, List.class);
        sendResponse(exchange, OK, jsonTasks);
    }


    @Override
    void processDelete(String path, HttpExchange exchange) {
        try {
            String[] elements = path.split("/");
            if(elements.length == PATH_WITHOUT_ID) {
                sendResponse(exchange, METHOD_NOT_ALLOWED, "DELETE не поддерживается для /tasks");
            }
            if (elements.length == PATH_WITH_ID ) {
                if(isNumber(elements[ID_IN_PATH])){
                    int id = Integer.parseInt(elements[ID_IN_PATH]);
                    deleteOneTask(exchange, id);
                    return;
                }
                sendResponse(exchange, BAD_REQUEST, BAD_REQUEST_MESSAGE);
                return;
            }
            sendResponse(exchange, NOT_FOUND, NOT_FOUND_MESSAGE);
        } catch (IOException e) {
            System.out.println(ANSWER_SERVER_EXCEPTION + e.getMessage());
            sendErrorResponse(exchange, INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void deleteOneTask(HttpExchange exchange, int id) throws IOException {
        Task task = manager.getTask(id);
        if (task == null) {
            sendResponse(exchange, NOT_FOUND, NOT_FOUND_MESSAGE);
            return;
        }
        manager.deleteTask(id);
        sendResponse(exchange, NO_CONTENT, "");
    }


    @Override
    void processPost(String path, HttpExchange exchange) {
        try {
            String[] elements = path.split("/");
            if (elements.length == PATH_WITHOUT_ID) {
                createOrUpdate(exchange);
                return;
            }
            sendResponse(exchange, BAD_REQUEST, BAD_REQUEST_MESSAGE);
        } catch (IOException e) {
            System.out.println(ANSWER_SERVER_EXCEPTION + e.getMessage());
            sendErrorResponse(exchange, INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (JsonErrorConverterException e) {
            sendErrorResponse(exchange, BAD_REQUEST, e.getMessage());
        } catch (IntersectionsException e) {
            sendErrorResponse(exchange, CONFLICT, e.getMessage());
        }
    }

    private void createOrUpdate(HttpExchange exchange) throws JsonErrorConverterException, IOException, IntersectionsException {
        try {
            byte[] body = exchange.getRequestBody().readAllBytes();
            if(body.length == 0){
                throw new JsonErrorConverterException(EMPTY_REQUEST_BODY);
            }
            String json = new String(body);
            Task task = jsonMapper.fromJson(json, Task.class);
            // если у задачи отсутствует id будем создавать новую задачу
            if (task.getId() == null) {
                Task createdTask = createTask(task);
                // переводим обратно в json
                String jsonTask = jsonMapper.toJson(createdTask, Task.class);
                // отправляем на сервер код ответа и json нашей задачи
                sendResponse(exchange, CREATED, jsonTask);
                return;
            }

            // обновляем задачу
            Task updatedTask = updateTask(task);
            if (updatedTask == null) {
                sendResponse(exchange, NOT_FOUND, NOT_FOUND_MESSAGE);
                return;
            }

            String taskJson = jsonMapper.toJson(updatedTask, Task.class);
            sendResponse(exchange, OK, taskJson);

        } catch (JsonSyntaxException e) {
            throw new JsonErrorConverterException(SERIALIZED_EXCEPTION_MESSAGE);
        }
    }

    private Task createTask(Task task) throws JsonErrorConverterException, IntersectionsException {
        // валидация на наличие названия задачи
        if (task.getName() == null) {
            throw new JsonErrorConverterException(NOT_HAVE_NAME_MESSAGE);
        }
        // Проверяем статус задачи, если нет, то указываем. Т.к. задачу будем создавать на основе той, что
        // в json, чтобы статус не стал null.
        if (task.getStatus() == null) {
            task.setStatus(Status.NEW);
        }
        // создаем задачу в менеджере
        return manager.createTask(task);
    }

    private Task updateTask(Task task) throws IntersectionsException {
        Task oldTask = manager.getTask(task.getId());
        if (oldTask == null) {
            return null;
        }
        if (task.getName() != null) {
            oldTask.setName(task.getName());
        }
        if (task.getDescription() != null) {
            oldTask.setDescription(task.getDescription());
        }
        if (task.getStatus() != null) {
            oldTask.setStatus(task.getStatus());
        }
        if (task.getStartTime() != null) {
            oldTask.setStartTime(task.getStartTime());
        }
        if (task.getDuration() != null) {
            oldTask.setDuration(task.getDuration());
        }
        // может выдать intersectionsException
        return manager.updateTask(oldTask);
    }

}
