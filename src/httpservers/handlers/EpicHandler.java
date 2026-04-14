package httpservers.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.IntersectionsException;
import exceptions.JsonErrorConverterException;
import manager.TaskManager;
import status.Status;
import tasks.Epic;

import java.io.IOException;

public class EpicHandler extends BaseHandler {

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    void processGet(String path, HttpExchange exchange) {

    }

    @Override
    void processPost(String path, HttpExchange exchange) {
        try {
            String[] elements = path.split("/");
            if (elements.length == 2) {
                createOrUpdate(exchange);
                return;
            }

            sendResponse(exchange, BAD_REQUEST, BAD_REQUEST_MESSAGE);

        } catch (IOException e) {
            System.out.println(ANSWER_SERVER_EXCEPTION + e.getMessage());
        } catch (IntersectionsException e) {
            try {
                sendResponse(exchange, NOT_ACCEPTABLE, e.getMessage());
            } catch (IOException exception) {
                System.out.println(ANSWER_SERVER_EXCEPTION + e.getMessage());
            }
        } catch (JsonErrorConverterException e) {
            try{
                sendResponse(exchange, BAD_REQUEST, e.getMessage());
            } catch (IOException exp) {
                System.out.println(ANSWER_SERVER_EXCEPTION + e.getMessage());
            }
        }
    }

    private void createOrUpdate(HttpExchange exchange) throws IOException, JsonErrorConverterException, IntersectionsException {
        try {
            byte[] body = exchange.getRequestBody().readAllBytes();
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

    }
}
