package httpservers.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import httpservers.adapters.DurationAdapter;
import httpservers.adapters.InstantAdapter;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

public abstract class BaseHandler implements HttpHandler {
    protected TaskManager manager;
    protected Gson jsonMapper;
    protected final int OK = 200;
    protected final int CREATED = 201;
    protected final int NOT_FOUND = 404;
    protected final int METHOD_NOT_ALLOWED = 405;
    protected final int CONFLICT = 406;
    protected final int INTERNAL_SERVER_ERROR = 500;
    protected final int BAD_REQUEST = 400;
    protected final int NO_CONTENT = 204;
    protected static final String NOT_FOUND_MESSAGE = "Такой задачи не существует";
    protected static final String NOT_HAVE_NAME_MESSAGE = "Отсутствует название задачи";
    protected static final String SERIALIZED_EXCEPTION_MESSAGE = "Ошибка при попытке десериализации тела запроса";
    protected static final String ANSWER_SERVER_EXCEPTION = "Ошибка при передаче ответа серверу";
    protected static final String BAD_REQUEST_MESSAGE = "Неверно указан адрес запроса";
    protected static final String EMPTY_REQUEST_BODY = "Отсутствует тело запроса";


    public BaseHandler(TaskManager manager) {
        this.manager = manager;
        jsonMapper = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        try {
            switch (method) {
                case "GET":
                    processGet(path, exchange);
                    break;
                case "POST":
                    processPost(path, exchange);
                    break;
                case "DELETE":
                    processDelete(path, exchange);
                    break;
                default: sendResponse(exchange, METHOD_NOT_ALLOWED, "Method not allowed");
            }
        } catch (IOException e) {
            try {
                sendResponse(exchange, INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
            } catch (IOException ex) {
                System.out.println("Не удалось отправить ответ: " + ex.getMessage());
            }
        }
    }

    abstract void processGet(String path, HttpExchange exchange) ;

    abstract void processPost(String path, HttpExchange exchange);

    abstract void processDelete(String path, HttpExchange exchange);


    protected void sendResponse(HttpExchange ex, int code, String message) throws IOException {
        ex.getResponseHeaders().add("Content-Type", "application/json");
        if(code == NO_CONTENT) {
            ex.sendResponseHeaders(code, -1);
        } else {
            byte[] responseBody = message.getBytes(StandardCharsets.UTF_8);
            ex.sendResponseHeaders(code, responseBody.length);
            ex.getResponseBody().write(responseBody);
        }
        ex.getResponseBody().flush();
        ex.close();
    }


    protected boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    protected void sendErrorResponse(HttpExchange exchange, int code, String message) {
        try {
            sendResponse(exchange, code, message);
        } catch (IOException e) {
            System.out.println(ANSWER_SERVER_EXCEPTION + e.getMessage());
        }
    }



}
