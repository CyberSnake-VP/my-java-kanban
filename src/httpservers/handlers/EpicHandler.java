package httpservers.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.IntersectionsException;
import exceptions.JsonErrorConverterException;
import manager.TaskManager;
import status.Status;
import tasks.Epic;

import java.io.IOException;
import java.util.List;

public class EpicHandler extends BaseHandler {

    private static final int PATH_WITH_ID = 3;
    private static final int ID_IN_PATH = 2;
    private static final int PATH_WITHOUT_ID = 2;

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    void processGet(String path, HttpExchange exchange) {
        try {
            String[] elements = path.split("/");
            if (elements.length == PATH_WITH_ID) {
                if (isNumber(elements[ID_IN_PATH])) {
                    int id = Integer.parseInt(elements[ID_IN_PATH]);
                    getOneEpic(exchange, id);
                    return;
                }
                sendResponse(exchange, BAD_REQUEST, BAD_REQUEST_MESSAGE);
                return;
            }
            if (elements.length == PATH_WITHOUT_ID) {
                getAllEpics(exchange);
                return;
            }

            sendResponse(exchange, NOT_FOUND, NOT_FOUND_MESSAGE);
        } catch (IOException e) {
            System.out.println(ANSWER_SERVER_EXCEPTION + e.getMessage());
            sendErrorResponse(exchange, INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void getAllEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = manager.getEpics();
        String json = jsonMapper.toJson(epics, List.class);
        sendResponse(exchange, OK, json);
    }

    private void getOneEpic(HttpExchange exchange, int id) throws IOException {
        Epic epic = manager.getEpic(id);
        if (epic == null) {
            sendResponse(exchange, NOT_FOUND, NOT_FOUND_MESSAGE);
            return;
        }
        String json = jsonMapper.toJson(epic, Epic.class);
        sendResponse(exchange, OK, json);
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
            sendErrorResponse(exchange, INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (IntersectionsException e) {
          sendErrorResponse(exchange, CONFLICT, e.getMessage());
        } catch (JsonErrorConverterException e) {
           sendErrorResponse(exchange, BAD_REQUEST, e.getMessage());
        }
    }

    private void createOrUpdate(HttpExchange exchange) throws IOException, JsonErrorConverterException, IntersectionsException {
        try {
            byte[] body = exchange.getRequestBody().readAllBytes();
            if(body.length == 0){
                throw new JsonErrorConverterException(EMPTY_REQUEST_BODY);
            }
            String bodyString = new String(body);
            Epic epic = jsonMapper.fromJson(bodyString, Epic.class);
            if (epic.getId() == null) {
                Epic createdEpic = createEpic(epic);
                String jsonEpic = jsonMapper.toJson(createdEpic, Epic.class);
                sendResponse(exchange, CREATED, jsonEpic);
                return;
            }

            Epic updatedEpic = updateEpic(epic);
            String jsonEpic = jsonMapper.toJson(updatedEpic, Epic.class);
            sendResponse(exchange, OK, jsonEpic);

        } catch (JsonSyntaxException e) {
            throw new JsonErrorConverterException(SERIALIZED_EXCEPTION_MESSAGE);
        }
    }

    private Epic createEpic(Epic epic) throws JsonErrorConverterException, IntersectionsException {
        // должно быть название
        if (epic.getName() == null) {
            throw new JsonErrorConverterException(NOT_HAVE_NAME_MESSAGE);
        }
        // нужно установить статус вручную, чтобы через конструктор не установлен был null
        if (epic.getStatus() == null) {
            epic.setStatus(Status.NEW);
        }
        // создание эпика
        return manager.createEpic(epic);
    }

    private Epic updateEpic(Epic epic) throws IntersectionsException {
        Epic oldEpic = manager.getEpic(epic.getId());

        if (oldEpic == null) {
            return null;
        }
        if (epic.getName() != null) {
            oldEpic.setName(epic.getName());
        }
        if (epic.getDescription() != null) {
            oldEpic.setDescription(epic.getDescription());
        }
        return manager.updateEpic(oldEpic);
    }

    @Override
    void processDelete(String path, HttpExchange exchange) {
        try{
            String[] elements = path.split("/");

            if(elements.length == PATH_WITHOUT_ID) {
                sendResponse(exchange, METHOD_NOT_ALLOWED, "Не поддерживается удаление через /epics");
                return;
            }
            if (elements.length == PATH_WITH_ID) {
                if(isNumber(elements[ID_IN_PATH])) {
                    int id = Integer.parseInt(elements[ID_IN_PATH]);
                    deleteOneEpic(exchange, id);
                    return;
                }
                sendResponse(exchange, BAD_REQUEST, BAD_REQUEST_MESSAGE);
                return;
            }
            sendResponse(exchange, NOT_FOUND, NOT_FOUND_MESSAGE);
        }catch (IOException e){
            System.out.println(ANSWER_SERVER_EXCEPTION + e.getMessage());
            sendErrorResponse(exchange, INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void deleteOneEpic(HttpExchange exchange, int id) throws IOException {
        Epic epic = manager.getEpic(id);
        if (epic == null) {
            sendResponse(exchange, NOT_FOUND, NOT_FOUND_MESSAGE);
            return;
        }
        manager.deleteEpic(epic.getId());
        sendResponse(exchange, NO_CONTENT, "");
    }
}
