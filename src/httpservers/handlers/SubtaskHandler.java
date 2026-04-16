package httpservers.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.IntersectionsException;
import exceptions.JsonErrorConverterException;
import manager.TaskManager;
import status.Status;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.util.List;

public class SubtaskHandler extends BaseHandler{
  private static final int PATH_WITHOUT_ID = 2;
  private static final int PATH_WITH_ID = 3;
  private static final int ID_IN_PATH = 2;
  private static final String NOT_EPIC_ID = "Отсутствует id эпика";
  private static final String EPIC_NOT_FOUND = "Эпик для подзадачи не найден";


    public SubtaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    void processGet(String path, HttpExchange exchange) {
        try {
            String[] elements = path.split("/");
            if(elements.length == PATH_WITH_ID) {
                if(isNumber(elements[ID_IN_PATH])) {
                    int id =  Integer.parseInt(elements[ID_IN_PATH]);
                    getOneSubtask(exchange, id);
                    return;
                }
                sendResponse(exchange, BAD_REQUEST, BAD_REQUEST_MESSAGE);
                return;
            }
            if(elements.length == PATH_WITHOUT_ID) {
                getAllSubtasks(exchange);
                return;
            }
            sendResponse(exchange, NOT_FOUND, NOT_FOUND_MESSAGE);

        } catch (IOException e) {
            sendErrorResponse(exchange, INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void getAllSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = manager.getSubtasks();
        String json = jsonMapper.toJson(subtasks, List.class);
        sendResponse(exchange, OK, json);
    }

    private void getOneSubtask(HttpExchange exchange, int id) throws IOException {
       Subtask subtask = manager.getSubtask(id);
       if(subtask == null) {
           sendResponse(exchange, NOT_FOUND, NOT_FOUND_MESSAGE);
           return;
       }
       String json = jsonMapper.toJson(subtask, Subtask.class);
       sendResponse(exchange, OK, json);
    }

    @Override
    void processPost(String path, HttpExchange exchange) {
        try{
            String[] elements = path.split("/");
            if(elements.length == PATH_WITHOUT_ID) {
                createOrUpdate(exchange);
                return;
            }
            sendResponse(exchange, BAD_REQUEST, BAD_REQUEST_MESSAGE);
        } catch (IOException e) {
            sendErrorResponse(exchange, INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (JsonErrorConverterException e) {
            sendErrorResponse(exchange, BAD_REQUEST, e.getMessage());
        } catch (IntersectionsException e) {
            sendErrorResponse(exchange, CONFLICT, e.getMessage());
        }
    }

    private void createOrUpdate(HttpExchange exchange) throws IOException, JsonErrorConverterException, IntersectionsException {
       try {
           byte[] body = exchange.getRequestBody().readAllBytes();
           if(body.length == 0){
               throw new JsonErrorConverterException(EMPTY_REQUEST_BODY);
           }
           String bodyString = new String(body);
           Subtask subtask = jsonMapper.fromJson(bodyString, Subtask.class);
           if(subtask.getId() == null) {
               subtask = createSubtask(subtask);
               String jsonSubtask = jsonMapper.toJson(subtask);
               sendResponse(exchange, CREATED, jsonSubtask);
               return;
           }
           subtask = updateSubtask(subtask);
           if(subtask == null) {
               sendResponse(exchange, NOT_FOUND, NOT_FOUND_MESSAGE);
               return;
           }
           String jsonSubtask = jsonMapper.toJson(subtask);
           sendResponse(exchange, OK, jsonSubtask);

       } catch (JsonSyntaxException e) {
           throw new JsonErrorConverterException(SERIALIZED_EXCEPTION_MESSAGE);
       }
    }

    private Subtask updateSubtask(Subtask subtask) throws IntersectionsException{
        Subtask oldSubtask = manager.getSubtask(subtask.getId());

        if(oldSubtask == null) {
           return null;
        }
        if(subtask.getName() != null) {
            oldSubtask.setName(subtask.getName());
        }
        if(subtask.getDescription() != null) {
            oldSubtask.setDescription(subtask.getDescription());
        }
        if(subtask.getStatus() != null) {
            oldSubtask.setStatus(subtask.getStatus());
        }
        if(subtask.getStartTime() != null) {
            oldSubtask.setStartTime(subtask.getStartTime());
        }
        if(subtask.getDuration() != null) {
            oldSubtask.setDuration(subtask.getDuration());
        }

       return manager.updateSubtask(oldSubtask);
    }

    private Subtask createSubtask(Subtask subtask) throws JsonErrorConverterException, IntersectionsException  {
      if(subtask.getName() == null) {
          throw new JsonErrorConverterException(NOT_HAVE_NAME_MESSAGE);
      }
      if(subtask.getEpicId() == null) {
          throw new JsonErrorConverterException(NOT_EPIC_ID);
      }
      if(subtask.getStatus() == null) {
          subtask.setStatus(Status.NEW);
      }
      Epic epic = manager.getEpic(subtask.getEpicId());
      if(epic == null) {
          throw new JsonErrorConverterException(EPIC_NOT_FOUND);
      }

      return manager.createSubtask(subtask);
    }


    @Override
    void processDelete(String path, HttpExchange exchange) {

    }


}
